package com.bubblecloud.biz.oa.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.ApproveApplyMapper;
import com.bubblecloud.biz.oa.mapper.ApproveContentMapper;
import com.bubblecloud.biz.oa.mapper.ApproveMapper;
import com.bubblecloud.biz.oa.mapper.AttendanceApplyRecordMapper;
import com.bubblecloud.biz.oa.mapper.AttendanceStatisticsMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.bubblecloud.oa.api.entity.Approve;
import com.bubblecloud.oa.api.entity.ApproveApply;
import com.bubblecloud.oa.api.entity.ApproveContent;
import com.bubblecloud.oa.api.entity.AttendanceApplyRecord;
import com.bubblecloud.oa.api.entity.AttendanceStatistics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 审批通过写入 {@code eb_attendance_apply_record}，并对齐 PHP {@code calcApplyRecordTime} （原 PHP
 * {@code AttendanceApplyRecordService::createRecord} / {@code calcApplyRecordTime}）。
 *
 * @author qinlei
 * @date 2026/4/7 17:35
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceApplyRecordSyncService {

	private static final ZoneId TZ = ZoneId.of("Asia/Shanghai");

	private static final int PERSONNEL_HOLIDAY = 1;

	private static final int PERSONNEL_SIGN = 2;

	private static final int PERSONNEL_OVERTIME = 3;

	private static final int PERSONNEL_OUT = 4;

	private static final int PERSONNEL_TRIP = 5;

	private static final int APPROVE_PASSED = 1;

	private final ApproveApplyMapper approveApplyMapper;

	private final ApproveMapper approveMapper;

	private final ApproveContentMapper approveContentMapper;

	private final AttendanceApplyRecordMapper attendanceApplyRecordMapper;

	private final AttendanceStatisticsMapper attendanceStatisticsMapper;

	private final AttendanceArrangeService attendanceArrangeService;

	private final AttendanceStatisticsApproveHook attendanceStatisticsApproveHook;

	private final ObjectMapper objectMapper;

	/**
	 * 审批通过后生成考勤核算记录（对齐 PHP {@code AttendanceApplyRecordService::createApplyRecord}）。
	 */
	@Transactional(rollbackFor = Exception.class)
	public void createFromPassedApply(Long applyId) {
		if (applyId == null) {
			return;
		}
		ApproveApply apply = approveApplyMapper.selectById(applyId);
		if (apply == null || !Integer.valueOf(APPROVE_PASSED).equals(apply.getStatus())) {
			return;
		}
		if (apply.getCrudId() != null && apply.getCrudId() > 0) {
			log.info("审批 applyId={} 为低代码关联，跳过考勤核算写入", applyId);
			return;
		}
		if (attendanceApplyRecordMapper.countByApplyId(applyId) > 0) {
			return;
		}
		Approve cfg = approveMapper.selectById(apply.getApproveId());
		if (cfg == null || cfg.getTypes() == null) {
			return;
		}
		int approveType = cfg.getTypes();
		if (approveType != PERSONNEL_HOLIDAY && approveType != PERSONNEL_SIGN && approveType != PERSONNEL_OVERTIME
				&& approveType != PERSONNEL_OUT && approveType != PERSONNEL_TRIP) {
			return;
		}
		List<ApproveContent> rows = approveContentMapper.selectList(Wrappers.lambdaQuery(ApproveContent.class)
			.eq(ApproveContent::getApplyId, applyId)
			.orderByAsc(ApproveContent::getSort)
			.orderByAsc(ApproveContent::getId));
		if (rows.isEmpty()) {
			log.warn("审批 applyId={} 无 eb_approve_content，跳过考勤核算", applyId);
			return;
		}
		Map<String, Object> others = new HashMap<>();
		BigDecimal duration = BigDecimal.ZERO;
		String timeType = "day";
		LocalDateTime start = null;
		LocalDateTime end = null;
		for (ApproveContent row : rows) {
			JsonNode val = parseValueNode(row.getValue());
			JsonNode content = parseJsonObject(row.getContent());
			String symbol = StrUtil.trimToEmpty(row.getSymbol());
			if ("attendanceExceptionDate".equals(symbol)) {
				int aid = val.asInt(0);
				if (aid > 0) {
					others.put("abnormal_id", aid);
				}
			}
			if ("attendanceExceptionRecord".equals(symbol)) {
				int rid = val.asInt(0);
				if (rid > 0) {
					others.put("record_id", rid);
				}
			}
			if (val.has("duration")) {
				if (val.get("duration").isNumber()) {
					duration = val.get("duration").decimalValue();
				}
				else if (val.get("duration").isTextual()) {
					try {
						duration = new BigDecimal(val.get("duration").asText());
					}
					catch (NumberFormatException ignored) {
					}
				}
			}
			if (val.has("dateStart") && val.has("dateEnd")) {
				TimeSpan span = parseTimeSpan(val);
				if (span != null) {
					timeType = span.timeType();
					start = span.start();
					end = span.end();
				}
			}
			if (approveType == PERSONNEL_OVERTIME && content.path("title").asText("").equals("加班补贴")) {
				String subsidy = val.isTextual() ? val.asText() : val.asText("");
				if (StrUtil.isNotBlank(subsidy)) {
					others.put("calc_type", "调休".equals(subsidy) ? 1 : 2);
				}
			}
			if (approveType == PERSONNEL_HOLIDAY && "holidayType".equals(symbol) && "select".equals(row.getTypes())) {
				int hid = val.asInt(0);
				if (hid > 0) {
					others.put("holiday_type_id", hid);
				}
			}
		}
		if (approveType == PERSONNEL_SIGN && others.containsKey("abnormal_id")) {
			int statId = ((Number) others.get("abnormal_id")).intValue();
			AttendanceStatistics st = attendanceStatisticsMapper.selectById((long) statId);
			if (st != null && st.getCreatedAt() != null) {
				LocalDate d = st.getCreatedAt().toLocalDate();
				start = d.atStartOfDay();
				end = d.atTime(LocalTime.MAX);
			}
		}
		if (start == null || end == null) {
			log.warn("审批 applyId={} 未能解析起止时间，跳过考勤核算", applyId);
			return;
		}
		LocalDate day = start.toLocalDate();
		int dateType = attendanceArrangeService.dayIsRest(apply.getUserId().intValue(), day) ? 2 : 1;
		String othersJson = writeOthers(others);
		AttendanceApplyRecord rec = new AttendanceApplyRecord();
		rec.setUid(apply.getUserId().intValue());
		rec.setWorkHours(duration);
		rec.setTimeType(timeType);
		rec.setDateType(dateType);
		rec.setApplyId(applyId.intValue());
		rec.setStartTime(start);
		rec.setEndTime(end);
		rec.setApplyType(approveType);
		rec.setTypeUnique("");
		rec.setOthers(othersJson);
		LocalDateTime now = LocalDateTime.now(TZ);
		rec.setCreatedAt(now);
		rec.setUpdatedAt(now);
		attendanceApplyRecordMapper.insert(rec);
		Map<String, Object> othersForHook = new HashMap<>(others);
		if (othersForHook.containsKey("record_id") && othersForHook.get("record_id") instanceof Number n) {
			int dec = n.intValue() - 1;
			if (dec > 0) {
				othersForHook.put("record_id", dec);
			}
		}
		attendanceStatisticsApproveHook.updateAbnormalShiftStatus(rec.getUid(), approveType, start, end,
				writeOthers(othersForHook));
		if (approveType == PERSONNEL_HOLIDAY && others.containsKey("holiday_type_id")) {
			long hid = ((Number) others.get("holiday_type_id")).longValue();
			attendanceStatisticsApproveHook.calcLeaveDurationByTime(rec.getUid(), rec.getId(), hid, start, end);
		}
	}

	/**
	 * 对齐 PHP {@code AttendanceApplyRecordService::calcApplyRecordTime}（按日切 compare_time）。
	 */
	@Transactional(rollbackFor = Exception.class)
	public void calcApplyRecordTime(LocalDate date) {
		if (date == null) {
			return;
		}
		LocalDateTime compare = date.atStartOfDay();
		List<Integer> types = List.of(PERSONNEL_HOLIDAY, PERSONNEL_OVERTIME, PERSONNEL_OUT, PERSONNEL_TRIP);
		List<AttendanceApplyRecord> list = attendanceApplyRecordMapper.selectByCompareTimeAndTypes(compare, types);
		if (list.isEmpty()) {
			return;
		}
		for (AttendanceApplyRecord item : list) {
			attendanceStatisticsApproveHook.updateAbnormalShiftStatus(item.getUid(), item.getApplyType(),
					item.getStartTime(), item.getEndTime(), item.getOthers());
			if (Integer.valueOf(PERSONNEL_HOLIDAY).equals(item.getApplyType())) {
				long hid = parseHolidayTypeId(item.getOthers());
				if (hid > 0 && item.getId() != null) {
					attendanceStatisticsApproveHook.calcLeaveDurationByTime(item.getUid(), item.getId(), hid,
							item.getStartTime(), item.getEndTime());
				}
			}
		}
	}

	private long parseHolidayTypeId(String othersJson) {
		if (StrUtil.isBlank(othersJson)) {
			return 0L;
		}
		try {
			JsonNode n = objectMapper.readTree(othersJson);
			JsonNode id = n.get("holiday_type_id");
			if (id == null || id.isNull()) {
				return 0L;
			}
			return id.asLong(0L);
		}
		catch (Exception e) {
			return 0L;
		}
	}

	private String writeOthers(Map<String, Object> others) {
		try {
			return objectMapper.writeValueAsString(others);
		}
		catch (Exception e) {
			return "{}";
		}
	}

	private JsonNode parseValueNode(String raw) {
		if (StrUtil.isBlank(raw)) {
			return objectMapper.createObjectNode();
		}
		String t = raw.trim();
		try {
			if (t.startsWith("{") || t.startsWith("[")) {
				return objectMapper.readTree(t);
			}
			if (t.matches("^-?\\d+(\\.\\d+)?$")) {
				return objectMapper.getNodeFactory().numberNode(new BigDecimal(t));
			}
			return objectMapper.getNodeFactory().textNode(t);
		}
		catch (Exception e) {
			return objectMapper.getNodeFactory().textNode(t);
		}
	}

	private JsonNode parseJsonObject(String raw) {
		if (StrUtil.isBlank(raw)) {
			return objectMapper.createObjectNode();
		}
		try {
			return objectMapper.readTree(raw);
		}
		catch (Exception e) {
			return objectMapper.createObjectNode();
		}
	}

	private TimeSpan parseTimeSpan(JsonNode val) {
		try {
			String tt = val.path("timeType").asText("day");
			if ("day".equals(tt)) {
				LocalDate s0 = parseDateOnly(val.get("dateStart").asText());
				LocalDate e0 = parseDateOnly(val.get("dateEnd").asText());
				boolean timeStart = val.path("timeStart").asBoolean(true);
				boolean timeEnd = val.path("timeEnd").asBoolean(false);
				LocalTime st = timeStart ? LocalTime.MIDNIGHT : LocalTime.NOON;
				LocalTime et = timeEnd ? LocalTime.of(12, 0, 1) : LocalTime.of(23, 59, 59);
				return new TimeSpan("day", LocalDateTime.of(s0, st), LocalDateTime.of(e0, et));
			}
			LocalDateTime s = parseDateTimeFlexible(val.get("dateStart").asText());
			LocalDateTime e = parseDateTimeFlexible(val.get("dateEnd").asText());
			return new TimeSpan("hour", s, e);
		}
		catch (Exception e) {
			return null;
		}
	}

	private LocalDate parseDateOnly(String s) {
		String t = s.length() >= 10 ? s.substring(0, 10) : s;
		return LocalDate.parse(t.replace('/', '-'));
	}

	private LocalDateTime parseDateTimeFlexible(String s) {
		if (StrUtil.isBlank(s)) {
			return null;
		}
		String t = s.trim().replace('/', '-');
		DateTimeFormatter[] fmts = { DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
				DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm:ss"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
				DateTimeFormatter.ISO_LOCAL_DATE_TIME };
		for (DateTimeFormatter f : fmts) {
			try {
				return LocalDateTime.parse(t, f);
			}
			catch (DateTimeParseException ignored) {
			}
		}
		return LocalDate.parse(t.substring(0, 10)).atStartOfDay();
	}

	private record TimeSpan(String timeType, LocalDateTime start, LocalDateTime end) {
	}

}
