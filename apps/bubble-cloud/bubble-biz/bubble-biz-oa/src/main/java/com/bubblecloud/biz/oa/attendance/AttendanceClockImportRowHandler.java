package com.bubblecloud.biz.oa.attendance;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.AdminMapper;
import com.bubblecloud.biz.oa.mapper.AttendanceApplyRecordMapper;
import com.bubblecloud.biz.oa.mapper.AttendanceClockRecordMapper;
import com.bubblecloud.biz.oa.mapper.AttendanceStatisticsMapper;
import com.bubblecloud.biz.oa.service.AttendanceGroupService;
import com.bubblecloud.oa.api.entity.Admin;
import com.bubblecloud.oa.api.entity.AttendanceApplyRecord;
import com.bubblecloud.oa.api.entity.AttendanceClockRecord;
import com.bubblecloud.oa.api.entity.AttendanceStatistics;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 单条考勤 Excel 导入（对齐 PHP {@code AttendanceClockService::singleImport} 白名单/休息班次分支；其余分支待补全）。
 *
 * @author qinlei
 * @date 2026/4/7 15:40
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceClockImportRowHandler {

	private static final List<String> SHIFT_LABELS = List.of("第一次上班", "第一次下班", "第二次上班", "第二次下班");

	private static final int APPLY_HOLIDAY = 1;

	private static final int APPLY_OUT = 4;

	private static final int APPLY_TRIP = 5;

	private final AdminMapper adminMapper;

	private final AttendanceStatisticsMapper statisticsMapper;

	private final AttendanceClockRecordMapper clockRecordMapper;

	private final AttendanceGroupService attendanceGroupService;

	private final AttendanceApplyRecordMapper attendanceApplyRecordMapper;

	private final ObjectMapper objectMapper;

	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public void importExcelRow(Map<String, Object> record) {
		String name = str(record.get("姓名"));
		if (StrUtil.isBlank(name)) {
			return;
		}
		Admin admin = adminMapper.selectOne(Wrappers.lambdaQuery(Admin.class)
			.eq(Admin::getName, name.trim())
			.isNull(Admin::getDeletedAt)
			.last("LIMIT 1"));
		if (admin == null) {
			log.warn("考勤导入跳过：未找到姓名={} 的员工", name);
			return;
		}
		int adminId = admin.getId().intValue();
		String timeRaw = str(record.get("时间"));
		String[] timeData = timeRaw.split("\\s+");
		if (timeData.length < 2) {
			log.error("考勤导入时间格式异常 record={}", record);
			return;
		}
		LocalDate rowDate = parseDateFlexible(timeData[0].trim());
		if (rowDate == null) {
			log.error("考勤导入日期解析失败 record={}", record);
			return;
		}
		LocalDateTime dayStart = rowDate.atStartOfDay();
		LocalDateTime dayEnd = rowDate.atTime(LocalTime.MAX);
		List<AttendanceStatistics> dayStats = statisticsMapper.selectByUidAndRange(adminId, dayStart, dayEnd);
		if (CollUtil.isEmpty(dayStats)) {
			log.warn("考勤导入跳过：uid={} 日期={} 无考勤日统计，请先产生当日统计", adminId, rowDate);
			return;
		}
		AttendanceStatistics statistics = dayStats.get(0);
		JsonNode shift = readShiftJson(statistics.getShiftData());
		int shiftNumber = shift.path("number").asInt(0);
		boolean whitelist = attendanceGroupService.listWhitelistMemberIds().contains(adminId);
		if (whitelist || shiftNumber <= 0) {
			for (int i = 0; i < 4; i++) {
				String cell = str(record.get(SHIFT_LABELS.get(i)));
				if (isBlankOrPlaceholder(cell)) {
					continue;
				}
				LocalDateTime clockTime = resolveClockDateTime(rowDate, cell.trim());
				if (clockTime == null) {
					continue;
				}
				insertClock(adminId, statistics, clockTime);
			}
			return;
		}
		int maxSlots = Math.min(shiftNumber * 2, 4);
		for (int i = 0; i < maxSlots; i++) {
			String cell = str(record.get(SHIFT_LABELS.get(i)));
			boolean invalidTime = isBlankOrPlaceholder(cell);
			JsonNode rules = shift.path("rules");
			JsonNode rule = i > 1 ? rules.path(1) : rules.path(0);
			if (rule.isMissingNode()) {
				continue;
			}
			LocalDate dateString = statistics.getCreatedAt() == null ? rowDate
					: statistics.getCreatedAt().toLocalDate();
			LocalDateTime workInstant = workAnchorForSlot(rule, dateString, i);
			AssociatedApprove ap = calcAssociatedApprove(adminId, workInstant);
			boolean freeByRule = (i == 1 || i == 3) && rule.path("free_clock").asInt(0) > 0;
			if (ap.freeClock() || freeByRule) {
				LocalDateTime clockTime;
				if (invalidTime) {
					clockTime = workInstant;
				}
				else {
					LocalDateTime parsed = resolveClockDateTime(rowDate, cell.trim());
					clockTime = parsed != null ? parsed : workInstant;
				}
				insertClock(adminId, statistics, clockTime);
				continue;
			}
			if (invalidTime) {
				log.debug("考勤导入缺卡位 i={} uid={} 日期={}（未写统计状态，待与 PHP 对齐）", i, adminId, rowDate);
				continue;
			}
			LocalDateTime clockTime = resolveClockDateTime(rowDate, cell.trim());
			if (clockTime == null) {
				continue;
			}
			insertClock(adminId, statistics, clockTime);
		}
	}

	private void insertClock(int adminId, AttendanceStatistics statistics, LocalDateTime clockTime) {
		AttendanceClockRecord r = new AttendanceClockRecord();
		r.setUid(adminId);
		r.setFrameId(statistics.getFrameId());
		r.setGroupId(statistics.getGroupId());
		r.setGroup(StrUtil.nullToEmpty(statistics.getGroup()));
		r.setShiftId(statistics.getShiftId() == null ? 0 : statistics.getShiftId());
		r.setShiftData(StrUtil.nullToEmpty(statistics.getShiftData()));
		r.setIsExternal(0);
		r.setAddress("");
		r.setLat("");
		r.setLng("");
		r.setRemark("");
		r.setImage("");
		LocalDateTime now = LocalDateTime.now(AttendanceShiftRuleValidation.zone());
		r.setCreatedAt(clockTime != null ? clockTime : now);
		r.setUpdatedAt(r.getCreatedAt());
		clockRecordMapper.insert(r);
	}

	private LocalDateTime workAnchorForSlot(JsonNode rule, LocalDate day, int slotIndex) {
		boolean work = slotIndex == 0 || slotIndex == 2;
		String hm = work ? rule.path("work_hours").asText("09:00") : rule.path("off_hours").asText("18:00");
		LocalTime lt = parseHm(hm);
		LocalDateTime t = LocalDateTime.of(day, lt);
		String dayAfterKey = work ? "first_day_after" : "second_day_after";
		if (rule.path(dayAfterKey).asInt(0) == 1) {
			t = t.plusDays(1);
		}
		return t;
	}

	private AssociatedApprove calcAssociatedApprove(int uid, LocalDateTime compareTime) {
		List<AttendanceApplyRecord> list = attendanceApplyRecordMapper.selectCoveringInstant(uid, compareTime);
		boolean freeClock = false;
		int locationStatus = 0;
		for (AttendanceApplyRecord item : list) {
			int t = item.getApplyType() == null ? 0 : item.getApplyType();
			if (t == APPLY_OUT || t == APPLY_TRIP) {
				locationStatus = 1;
			}
			if (t == APPLY_HOLIDAY) {
				freeClock = true;
				locationStatus = 1;
			}
		}
		return new AssociatedApprove(freeClock, locationStatus);
	}

	private record AssociatedApprove(boolean freeClock, int locationStatus) {
	}

	private static LocalTime parseHm(String hm) {
		if (StrUtil.isBlank(hm)) {
			return LocalTime.of(9, 0);
		}
		String[] ps = hm.split(":");
		try {
			int h = Integer.parseInt(ps[0].trim());
			int m = ps.length > 1 ? Integer.parseInt(ps[1].trim()) : 0;
			return LocalTime.of(h, m);
		}
		catch (Exception e) {
			return LocalTime.of(9, 0);
		}
	}

	private JsonNode readShiftJson(String shiftData) {
		if (StrUtil.isBlank(shiftData)) {
			return objectMapper.createObjectNode();
		}
		try {
			return objectMapper.readTree(shiftData);
		}
		catch (Exception e) {
			return objectMapper.createObjectNode();
		}
	}

	private static LocalDateTime resolveClockDateTime(LocalDate rowDate, String shiftTime) {
		if (StrUtil.isBlank(shiftTime)) {
			return null;
		}
		String t = shiftTime.trim();
		if (t.length() < 10) {
			LocalTime lt = parseTimeFlexible(t);
			return lt == null ? null : LocalDateTime.of(rowDate, lt);
		}
		String norm = t.replace('/', '-');
		DateTimeFormatter[] fmts = { DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
				DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm:ss"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
				DateTimeFormatter.ofPattern("yyyy-M-d HH:mm:ss"), DateTimeFormatter.ofPattern("yyyy-M-d HH:mm") };
		for (DateTimeFormatter f : fmts) {
			try {
				return LocalDateTime.parse(norm, f);
			}
			catch (DateTimeParseException ignored) {
			}
		}
		return null;
	}

	private static LocalTime parseTimeFlexible(String s) {
		DateTimeFormatter[] fmts = { DateTimeFormatter.ofPattern("HH:mm:ss"), DateTimeFormatter.ofPattern("H:mm:ss"),
				DateTimeFormatter.ofPattern("HH:mm"), DateTimeFormatter.ofPattern("H:mm") };
		for (DateTimeFormatter f : fmts) {
			try {
				return LocalTime.parse(s, f);
			}
			catch (DateTimeParseException ignored) {
			}
		}
		return null;
	}

	private static LocalDate parseDateFlexible(String s) {
		if (StrUtil.isBlank(s)) {
			return null;
		}
		String t = s.length() >= 10 ? s.substring(0, 10).replace('/', '-') : s.replace('/', '-');
		try {
			return LocalDate.parse(t, DateTimeFormatter.ISO_LOCAL_DATE);
		}
		catch (DateTimeParseException e) {
			try {
				return LocalDate.parse(t, DateTimeFormatter.ofPattern("yyyy-M-d"));
			}
			catch (DateTimeParseException e2) {
				return null;
			}
		}
	}

	private static boolean isBlankOrPlaceholder(String cell) {
		if (StrUtil.isBlank(cell)) {
			return true;
		}
		String t = cell.trim();
		return "--".equals(t) || "未打卡".equals(t);
	}

	private static String str(Object o) {
		return o == null ? "" : String.valueOf(o).trim();
	}

}
