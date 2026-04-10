package com.bubblecloud.biz.oa.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.attendance.AttendanceClockImportAsyncService;
import com.bubblecloud.biz.oa.attendance.AttendanceClockRecordSearchQuery;
import com.bubblecloud.biz.oa.attendance.AttendanceShiftRuleValidation;
import com.bubblecloud.biz.oa.attendance.AttendanceStatisticsSearchQuery;
import com.bubblecloud.biz.oa.attendance.AttendanceTimeScope;
import com.bubblecloud.biz.oa.constant.AttendanceClockConstants;
import com.bubblecloud.biz.oa.mapper.AdminMapper;
import com.bubblecloud.biz.oa.mapper.AttendanceApplyRecordMapper;
import com.bubblecloud.biz.oa.mapper.AttendanceClockRecordMapper;
import com.bubblecloud.biz.oa.mapper.AttendanceHandleRecordMapper;
import com.bubblecloud.biz.oa.mapper.AttendanceStatisticsMapper;
import com.bubblecloud.biz.oa.mapper.FrameAssistMapper;
import com.bubblecloud.biz.oa.mapper.FrameMapper;
import com.bubblecloud.biz.oa.service.ApproveHolidayTypeService;
import com.bubblecloud.biz.oa.service.AttendanceEntStatisticsService;
import com.bubblecloud.biz.oa.service.AttendanceGroupService;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.oa.api.dto.attendance.AttendanceStatisticsAdjustDTO;
import com.bubblecloud.oa.api.entity.Admin;
import com.bubblecloud.oa.api.entity.ApproveHolidayType;
import com.bubblecloud.oa.api.entity.AttendanceApplyRecord;
import com.bubblecloud.oa.api.entity.AttendanceClockRecord;
import com.bubblecloud.oa.api.entity.AttendanceGroup;
import com.bubblecloud.oa.api.entity.AttendanceHandleRecord;
import com.bubblecloud.oa.api.entity.AttendanceStatistics;
import com.bubblecloud.oa.api.entity.Frame;
import com.bubblecloud.oa.api.entity.FrameAssist;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 考勤统计、打卡、异常日期（对齐 PHP 考勤统计/打卡；请假等时长来自 {@code eb_attendance_apply_record}，导入走异步分块）。
 *
 * @author qinlei
 * @date 2026/4/7
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceEntStatisticsServiceImpl implements AttendanceEntStatisticsService {

	private static final Long DEFAULT_ENT_ID = 1L;

	private static final Pattern MONTH_YM = Pattern.compile("^[0-9]{4}-[0-9]{1,2}$");

	private static final String[] SHIFT_FIELDS = AttendanceClockConstants.SHIFT_PREFIXES;

	private static final List<String> IMPORT_FIELDS = List.of("时间", "姓名", "第一次上班", "第一次下班", "第二次上班", "第二次下班");

	private static final int APPLY_HOLIDAY = 1;

	private static final int APPLY_SIGN = 2;

	private static final int APPLY_OVERTIME = 3;

	private static final int APPLY_OUT = 4;

	private static final int APPLY_TRIP = 5;

	private final AttendanceStatisticsMapper statisticsMapper;

	private final AttendanceClockRecordMapper clockRecordMapper;

	private final AttendanceHandleRecordMapper handleRecordMapper;

	private final AttendanceGroupService attendanceGroupService;

	private final AdminMapper adminMapper;

	private final FrameMapper frameMapper;

	private final FrameAssistMapper frameAssistMapper;

	private final ObjectMapper objectMapper;

	private final AttendanceClockImportAsyncService attendanceClockImportAsyncService;

	private final AttendanceApplyRecordMapper attendanceApplyRecordMapper;

	private final ApproveHolidayTypeService approveHolidayTypeService;

	@Override
	public Map<String, Object> dailyStatistics(Pg<Object> pg, Long viewerId, Integer scope,
			List<Integer> personnelStatus, Integer frameId, Integer groupId, String time, List<Integer> userId,
			int filterType) {
		AttendanceStatisticsSearchQuery q = buildSearchQuery(viewerId, scope, frameId, groupId, userId, filterType);
		applyPersonnelStatus(q, personnelStatus);
		AttendanceTimeScope.Range range = AttendanceTimeScope.resolveCreatedRange(time);
		q.setRangeStart(range.startInclusive());
		q.setRangeEnd(range.endInclusive());
		long page = pg.getCurrent() > 0 ? pg.getCurrent() : 1;
		long size = pg.getSize() > 0 ? pg.getSize() : 10;
		long offset = (page - 1) * size;
		long total = statisticsMapper.countDailySearch(q);
		List<AttendanceStatistics> rows = statisticsMapper.selectDailySearch(q, offset, size);
		List<Map<String, Object>> list = new ArrayList<>();
		for (AttendanceStatistics row : rows) {
			list.add(toDailyRowMap(row));
		}
		return listData(list, total);
	}

	@Override
	public Map<String, Object> monthlyStatistics(Pg<Object> pg, Long viewerId, Integer scope,
			List<Integer> personnelStatus, Integer frameId, Integer groupId, String month) {
		String ym = StrUtil.trimToEmpty(month);
		if (StrUtil.isNotBlank(ym) && !MONTH_YM.matcher(ym).find()) {
			throw new IllegalArgumentException("考勤时间格式错误");
		}
		if (StrUtil.isBlank(ym)) {
			ym = LocalDate.now(AttendanceShiftRuleValidation.zone()).format(DateTimeFormatter.ofPattern("yyyy-MM"));
		}
		AttendanceStatisticsSearchQuery q = buildSearchQuery(viewerId, scope, frameId, groupId, List.of(), 0);
		applyPersonnelStatus(q, personnelStatus);
		q.setMonthYm(ym);
		long page = pg.getCurrent() > 0 ? pg.getCurrent() : 1;
		long size = pg.getSize() > 0 ? pg.getSize() : 10;
		long offset = (page - 1) * size;
		long total = statisticsMapper.countDistinctUidMonthly(q);
		List<AttendanceStatistics> rows = statisticsMapper.selectMonthlyRows(q, offset, size);
		List<Map<String, Object>> list = new ArrayList<>();
		for (AttendanceStatistics row : rows) {
			list.add(toMonthlyRowMap(row, ym));
		}
		Map<String, Object> out = listData(list, total);
		out.put("holiday_type", listHolidayTypesMeta());
		return out;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveStatisticsResult(Long viewerId, long statisticsId, AttendanceStatisticsAdjustDTO dto) {
		if (dto == null || dto.getNumber() == null) {
			throw new IllegalArgumentException("common.empty.attrs");
		}
		int num = dto.getNumber();
		if (num < 0 || num > 3) {
			throw new IllegalArgumentException("common.empty.attrs");
		}
		AttendanceStatistics info = statisticsMapper.selectById(statisticsId);
		if (info == null) {
			throw new IllegalArgumentException("common.operation.noExists");
		}
		int st = dto.getStatus() == null ? 0 : dto.getStatus();
		int loc = dto.getLocationStatus() == null ? 0 : dto.getLocationStatus();
		String prefix = SHIFT_FIELDS[num];
		Integer beforeStatus = shiftStatusGet(info, num);
		Integer beforeLoc = shiftLocationGet(info, num);
		if (beforeStatus != null && beforeStatus < 1) {
			loc = 0;
		}
		if (!Objects.equals(st, beforeStatus)) {
			if (info.getShiftId() != null && info.getShiftId() > 1) {
				ClockAdjust clock = computeClockTimeForStatus(info, num, st);
				shiftTimeSet(info, num, clock.time());
				shiftIsAfterSet(info, num, clock.isAfter());
			}
			shiftStatusSet(info, num, st);
		}
		if (loc > 0 && !Objects.equals(loc, beforeLoc)) {
			shiftLocationSet(info, num, loc);
		}
		String resultText = buildResultText(beforeStatus == null ? 0 : beforeStatus, st,
				beforeLoc == null ? 0 : beforeLoc, loc);
		statisticsMapper.updateById(info);
		AttendanceHandleRecord h = new AttendanceHandleRecord();
		h.setStatisticsId(statisticsId);
		h.setShiftNumber(num);
		h.setRemark(StrUtil.nullToEmpty(dto.getRemark()));
		h.setResult(resultText);
		h.setSource(0);
		h.setBeforeStatus(beforeStatus == null ? 0 : beforeStatus);
		h.setBeforeLocationStatus(beforeLoc == null ? 0 : beforeLoc);
		h.setAfterStatus(st);
		h.setAfterLocationStatus(loc);
		h.setUid(viewerId == null ? 0 : viewerId.intValue());
		handleRecordMapper.insert(h);
	}

	@Override
	public Map<String, Object> handleRecordList(Pg<Object> pg, Long viewerId, long statisticsId) {
		AttendanceStatistics st = statisticsMapper.selectById(statisticsId);
		if (st == null) {
			throw new IllegalArgumentException("common.operation.noExists");
		}
		assertCanViewStatistics(viewerId, st.getUid());
		long page = pg.getCurrent() > 0 ? pg.getCurrent() : 1;
		long size = pg.getSize() > 0 ? pg.getSize() : 10;
		long offset = (page - 1) * size;
		long total = handleRecordMapper.countByStatisticsId(statisticsId);
		List<AttendanceHandleRecord> rows = handleRecordMapper.selectPageByStatisticsId(statisticsId, offset, size);
		List<Map<String, Object>> list = new ArrayList<>();
		for (AttendanceHandleRecord r : rows) {
			list.add(toHandleRow(r));
		}
		return listData(list, total);
	}

	@Override
	public Map<String, Object> attendanceStatistics(Long viewerId, Integer userId, String time) {
		List<Integer> uids = userId != null && userId > 0 ? List.of(userId) : List.of();
		AttendanceStatisticsSearchQuery scope = buildSearchQuery(viewerId, null, null, null, uids, 1);
		int targetUid = scope.getUidIn().get(0);
		AttendanceTimeScope.Range range = AttendanceTimeScope.resolveAttendanceSummaryRange(time);
		LackAgg agg = aggregateLackForRange(targetUid, range.startInclusive(), range.endInclusive());
		Map<String, Object> m = new LinkedHashMap<>();
		m.put("required_days", 0);
		m.put("normal_days", statisticsMapper.countNormalDays(targetUid, range.startInclusive(), range.endInclusive()));
		m.put("work_hours", formatOneDecimal(
				statisticsMapper.avgActualWorkHours(targetUid, range.startInclusive(), range.endInclusive())));
		m.put("leave_hours", fmtApplyHours(attendanceApplyRecordMapper.sumWorkHours(targetUid, range.startInclusive(),
				range.endInclusive(), APPLY_HOLIDAY)));
		m.put("out_hours", fmtApplyHours(attendanceApplyRecordMapper.sumWorkHours(targetUid, range.startInclusive(),
				range.endInclusive(), APPLY_OUT)));
		m.put("trip_hours", fmtApplyHours(attendanceApplyRecordMapper.sumWorkHours(targetUid, range.startInclusive(),
				range.endInclusive(), APPLY_TRIP)));
		m.put("overtime_hours", fmtApplyHours(attendanceApplyRecordMapper.sumWorkHours(targetUid,
				range.startInclusive(), range.endInclusive(), APPLY_OVERTIME)));
		m.put("sign", attendanceApplyRecordMapper.countByUidRangeType(targetUid, range.startInclusive(),
				range.endInclusive(), APPLY_SIGN));
		m.put("late", statisticsMapper.countAnyShiftStatusInRange(targetUid, range.startInclusive(),
				range.endInclusive(), List.of(AttendanceClockConstants.LATE)));
		m.put("extreme_late", statisticsMapper.countAnyShiftStatusInRange(targetUid, range.startInclusive(),
				range.endInclusive(), List.of(AttendanceClockConstants.EXTREME_LATE)));
		m.put("early_leave", statisticsMapper.countAnyShiftStatusInRange(targetUid, range.startInclusive(),
				range.endInclusive(), List.of(AttendanceClockConstants.LEAVE_EARLY)));
		m.put("lack_card", agg.lateCard + agg.earlyCard);
		m.put("absenteeism", agg.absenteeism);
		m.put("location_abnormal", agg.locationAbnormal);
		return m;
	}

	@Override
	public Map<String, Object> individualStatistics(Pg<Object> pg, Long viewerId, List<Integer> personnelStatus,
			List<Integer> userId, String time) {
		return dailyStatistics(pg, viewerId, null, personnelStatus, null, null, time, userId, 1);
	}

	@Override
	public Map<String, Object> clockRecordList(Pg<Object> pg, Integer scope, Integer frameId, Integer groupId,
			String time, Integer uid) {
		AttendanceClockRecordSearchQuery q = new AttendanceClockRecordSearchQuery();
		if (uid != null && uid > 0) {
			q.setUidSingle(uid);
		}
		if (frameId != null && frameId > 0) {
			q.setFrameId(frameId);
		}
		if (groupId != null && groupId > 0) {
			q.setGroupId(groupId);
		}
		AttendanceTimeScope.Range range = AttendanceTimeScope.resolveCreatedRange(time);
		q.setRangeStart(range.startInclusive());
		q.setRangeEnd(range.endInclusive());
		long page = pg.getCurrent() > 0 ? pg.getCurrent() : 1;
		long size = pg.getSize() > 0 ? pg.getSize() : 10;
		long offset = (page - 1) * size;
		long total = clockRecordMapper.countSearch(q);
		List<AttendanceClockRecord> rows = clockRecordMapper.selectSearch(q, offset, size);
		List<Map<String, Object>> list = new ArrayList<>();
		for (AttendanceClockRecord r : rows) {
			list.add(toClockListRow(r));
		}
		return listData(list, total);
	}

	@Override
	public Map<String, Object> clockRecordInfo(long id) {
		AttendanceClockRecord r = clockRecordMapper.selectById(id);
		if (r == null) {
			throw new IllegalArgumentException("common.operation.noExists");
		}
		Map<String, Object> m = objectMapper.convertValue(r, new TypeReference<>() {
		});
		m.put("card", briefCard(r.getUid()));
		m.put("frame", briefFrame(r.getFrameId()));
		return m;
	}

	@Override
	public List<Map<String, Object>> abnormalDateList(Long viewerId) {
		if (viewerId == null) {
			return List.of();
		}
		int uid = viewerId.intValue();
		AttendanceGroup group = attendanceGroupService.findFirstGroupForAdmin(uid);
		AttendanceStatisticsSearchQuery q = new AttendanceStatisticsSearchQuery();
		q.setUidIn(List.of(uid));
		if (group != null && !repairAllowed(group)) {
			return List.of();
		}
		fillAbnormalRepairFilter(q, group);
		LocalDate today = LocalDate.now(AttendanceShiftRuleValidation.zone());
		if (group != null && ObjectUtil.equals(group.getIsLimitTime(), 1)) {
			int lt = group.getLimitTime() == null ? 0 : group.getLimitTime();
			if (lt < 1) {
				q.setRangeStart(today.atStartOfDay());
				q.setRangeEnd(LocalDateTime.of(today, java.time.LocalTime.MAX));
			}
			else {
				LocalDate start = today.minusDays(lt);
				q.setRangeStart(start.atStartOfDay());
				q.setRangeEnd(LocalDateTime.of(today, java.time.LocalTime.MAX));
			}
		}
		List<AttendanceStatistics> found = statisticsMapper.selectAbnormalCandidates(uid, q);
		List<Map<String, Object>> out = new ArrayList<>();
		for (AttendanceStatistics s : found) {
			Map<String, Object> opt = new LinkedHashMap<>();
			opt.put("value", s.getId());
			opt.put("label", s.getCreatedAt() == null ? "" : s.getCreatedAt().toLocalDate().toString());
			out.add(opt);
		}
		return out;
	}

	@Override
	public List<Map<String, Object>> abnormalRecordList(Long viewerId, long statisticsId) {
		AttendanceStatistics info = statisticsMapper.selectById(statisticsId);
		if (info == null || viewerId == null || !Objects.equals(info.getUid(), viewerId.intValue())) {
			throw new IllegalArgumentException("暂无可操作记录！");
		}
		JsonNode shift = readShiftJson(info.getShiftData());
		int shiftNum = shift.path("number").asInt(0) * 2;
		List<Map<String, Object>> select = new ArrayList<>();
		JsonNode rules = shift.path("rules");
		JsonNode rule0 = rules.isArray() && rules.size() > 0 ? rules.get(0) : null;
		JsonNode rule1 = rules.isArray() && rules.size() > 1 ? rules.get(1) : rule0;
		for (int i = 0; i < shiftNum; i++) {
			JsonNode rule = i > 1 ? rule1 : rule0;
			int free = rule != null ? rule.path("free_clock").asInt(0) : 0;
			if ((i == 1 || i == 3) && free > 0 && shiftStatusGet(info, i) == 0) {
				continue;
			}
			int st = shiftStatusGet(info, i);
			int ls = shiftLocationGet(info, i);
			if (st > 0 && st < 2 && ls < 2) {
				continue;
			}
			boolean workType = (i == 0 || i == 2);
			String ruleKey = workType ? "work_hours" : "off_hours";
			String t = rule != null ? rule.path(ruleKey).asText("") : "";
			Map<String, Object> row = new LinkedHashMap<>();
			row.put("shift_number", i);
			row.put("title", workType ? "上班" : "下班");
			row.put("time", t);
			select.add(row);
		}
		return select;
	}

	@Override
	public void importClockRecord(List<Map<String, Object>> data) {
		if (CollUtil.isEmpty(data)) {
			throw new IllegalArgumentException("common.empty.attrs");
		}
		Map<String, Object> first = data.get(0);
		for (String f : IMPORT_FIELDS) {
			if (!first.containsKey(f)) {
				throw new IllegalArgumentException(f + "数据不存在");
			}
		}
		attendanceClockImportAsyncService.scheduleExcelImport(data);
	}

	@Override
	public void importClockThirdParty(int type, List<Map<String, Object>> data) {
		if (type != 1 && type != 2) {
			throw new IllegalArgumentException("导入类型错误");
		}
		if (CollUtil.isEmpty(data)) {
			throw new IllegalArgumentException("导入内容不能为空");
		}
		attendanceClockImportAsyncService.scheduleThirdPartyImport(type, data);
	}

	private void assertCanViewStatistics(Long viewerId, Integer statUid) {
		if (viewerId == null || statUid == null) {
			throw new IllegalArgumentException("common.operation.noExists");
		}
		if (viewerId.intValue() == statUid) {
			return;
		}
		Set<Integer> net = new HashSet<>(attendanceGroupService.listNetworkMemberIds(viewerId));
		net.addAll(attendanceGroupService.listWhitelistMemberIds());
		if (!net.contains(statUid)) {
			throw new IllegalArgumentException("common.operation.noExists");
		}
	}

	private Map<String, Object> toHandleRow(AttendanceHandleRecord r) {
		Map<String, Object> m = new LinkedHashMap<>();
		m.put("id", r.getId());
		m.put("shift_number", r.getShiftNumber());
		m.put("result", r.getResult());
		m.put("remark", r.getRemark());
		m.put("source", r.getSource());
		m.put("uid", r.getUid());
		m.put("created_at", r.getCreatedAt());
		m.put("card", briefCard(r.getUid()));
		return m;
	}

	private Map<String, Object> toClockListRow(AttendanceClockRecord r) {
		Map<String, Object> m = new LinkedHashMap<>();
		m.put("id", r.getId());
		m.put("frame_id", r.getFrameId());
		m.put("group_id", r.getGroupId());
		m.put("group", r.getGroup());
		m.put("shift_id", r.getShiftId());
		m.put("uid", r.getUid());
		m.put("created_at", r.getCreatedAt());
		m.put("card", briefCard(r.getUid()));
		m.put("frame", briefFrame(r.getFrameId()));
		return m;
	}

	private Map<String, Object> briefCard(Integer adminId) {
		if (adminId == null) {
			return Map.of();
		}
		Admin a = adminMapper.selectById(adminId.longValue());
		if (a == null) {
			return Map.of();
		}
		Map<String, Object> c = new LinkedHashMap<>();
		c.put("id", a.getId());
		c.put("uid", a.getUid());
		c.put("name", a.getName());
		c.put("avatar", a.getAvatar());
		c.put("phone", a.getPhone());
		return c;
	}

	private Map<String, Object> briefFrame(Integer frameId) {
		if (frameId == null || frameId <= 0) {
			return Map.of();
		}
		Frame f = frameMapper.selectById(frameId.longValue());
		if (f == null) {
			return Map.of();
		}
		return Map.of("id", f.getId(), "name", f.getName());
	}

	private Map<String, Object> toDailyRowMap(AttendanceStatistics row) {
		Map<String, Object> m = entityToMap(row);
		for (String p : SHIFT_FIELDS) {
			m.put(p + "_shift_normal", 0);
		}
		m.put("leave_time", "0");
		m.put("overtime_work_hours", "0");
		m.put("card", briefCard(row.getUid()));
		m.put("frame", briefFrame(row.getFrameId()));
		JsonNode shift = readShiftJson(row.getShiftData());
		int sn = shift.path("number").asInt(0) * 2;
		if (row.getShiftId() != null && row.getShiftId() >= 2) {
			for (int i = 0; i < sn && i < 4; i++) {
				// 对齐 PHP：迟到/早退正常分钟数由班次服务计算，此处占位 0
				m.put(SHIFT_FIELDS[i] + "_shift_normal", 0);
			}
		}
		return m;
	}

	private Map<String, Object> toMonthlyRowMap(AttendanceStatistics row, String monthYm) {
		Map<String, Object> m = new LinkedHashMap<>();
		m.put("id", row.getId());
		m.put("uid", row.getUid());
		m.put("frame_id", row.getFrameId());
		m.put("card", briefCard(row.getUid()));
		LackAgg agg = aggregateLackForMonth(row.getUid(), monthYm);
		m.put("required_days", 0);
		m.put("actual_days", agg.actualDays);
		m.put("late", statisticsMapper.countAnyShiftStatusInRange(row.getUid(), monthStart(monthYm), monthEnd(monthYm),
				AttendanceClockConstants.ALL_LATE));
		m.put("leave_early", statisticsMapper.countAnyShiftStatusInRange(row.getUid(), monthStart(monthYm),
				monthEnd(monthYm), List.of(AttendanceClockConstants.LEAVE_EARLY)));
		m.put("late_card", agg.lateCard);
		m.put("early_card", agg.earlyCard);
		m.put("absenteeism", agg.absenteeism);
		m.put("overtime_hours", "0");
		m.put("trip_hours", "0");
		m.put("out_hours", "0");
		m.put("frame", briefFrame(row.getFrameId()));
		m.put("group", row.getGroup());
		m.put("holiday_data", buildHolidayDataForUid(row.getUid(), monthYm));
		return m;
	}

	private LocalDateTime monthStart(String ym) {
		LocalDate d = LocalDate.parse(ym + "-01", DateTimeFormatter.ISO_LOCAL_DATE);
		return d.atStartOfDay();
	}

	private LocalDateTime monthEnd(String ym) {
		LocalDate d = LocalDate.parse(ym + "-01", DateTimeFormatter.ISO_LOCAL_DATE);
		LocalDate last = d.with(java.time.temporal.TemporalAdjusters.lastDayOfMonth());
		return LocalDateTime.of(last, java.time.LocalTime.MAX);
	}

	private LackAgg aggregateLackForRange(int uid, LocalDateTime start, LocalDateTime end) {
		return lackFromRecords(statisticsMapper.selectByUidAndRange(uid, start, end));
	}

	private LackAgg aggregateLackForMonth(int uid, String monthYm) {
		return lackFromRecords(statisticsMapper.selectByUidAndMonth(uid, monthYm));
	}

	private LackAgg lackFromRecords(List<AttendanceStatistics> records) {
		BigDecimal actualDays = new BigDecimal("0.0");
		int lateCard = 0;
		int earlyCard = 0;
		int absenteeism = 0;
		int locationAbnormal = 0;
		for (AttendanceStatistics record : records) {
			if (record.getShiftId() == null || record.getShiftId() < 2) {
				continue;
			}
			JsonNode shift = readShiftJson(record.getShiftData());
			int shiftNum = shift.path("number").asInt(0) * 2;
			int lateNum = 0;
			int absentNum = 0;
			int earlyNum = 0;
			int locAb = 0;
			for (int i = 0; i < shiftNum && i < 4; i++) {
				int shiftStatus = shiftStatusGet(record, i);
				int shiftLoc = shiftLocationGet(record, i);
				if (shiftStatus == AttendanceClockConstants.LACK_CARD) {
					if (i == 0 || i == 2) {
						lateNum++;
					}
					else {
						earlyNum++;
					}
					absentNum++;
				}
				if (shiftStatus == AttendanceClockConstants.LATE_LACK_CARD) {
					lateNum++;
				}
				if (shiftStatus == AttendanceClockConstants.EARLY_LACK_CARD) {
					earlyNum++;
				}
				if (shiftLoc == AttendanceClockConstants.OFFICE_ABNORMAL) {
					locAb++;
				}
			}
			BigDecimal req = record.getRequiredWorkHours() == null ? BigDecimal.ZERO : record.getRequiredWorkHours();
			BigDecimal act = record.getActualWorkHours() == null ? BigDecimal.ZERO : record.getActualWorkHours();
			if (act.compareTo(new BigDecimal("0.1")) >= 0 && req.compareTo(new BigDecimal("0.1")) >= 0) {
				BigDecimal hours = act.divide(req, 2, RoundingMode.DOWN);
				BigDecimal portion = hours.multiply(BigDecimal.TEN)
					.setScale(0, RoundingMode.DOWN)
					.divide(BigDecimal.TEN, 1, RoundingMode.DOWN);
				actualDays = actualDays.add(portion);
			}
			if (absentNum == shiftNum) {
				absenteeism++;
			}
			else {
				lateCard += lateNum;
				earlyCard += earlyNum;
				locationAbnormal += locAb;
			}
		}
		return new LackAgg(absenteeism, lateCard, earlyCard, actualDays.toPlainString(), locationAbnormal);
	}

	private record LackAgg(int absenteeism, int lateCard, int earlyCard, String actualDays, int locationAbnormal) {
	}

	private AttendanceStatisticsSearchQuery buildSearchQuery(Long viewerId, Integer scope, Integer frameId,
			Integer groupId, List<Integer> userId, int filterType) {
		if (viewerId == null) {
			throw new IllegalArgumentException("用户未登录");
		}
		int self = viewerId.intValue();
		AttendanceStatisticsSearchQuery q = new AttendanceStatisticsSearchQuery();
		if (filterType == 1) {
			List<Integer> team = new ArrayList<>(attendanceGroupService.listNetworkMemberIds(viewerId));
			team.addAll(attendanceGroupService.listWhitelistMemberIds());
			Set<Integer> teamSet = new HashSet<>(team);
			List<Integer> req = CollUtil.isEmpty(userId) ? List.of()
					: userId.stream().filter(Objects::nonNull).toList();
			if (CollUtil.isNotEmpty(req)) {
				for (Integer u : req) {
					if (!Objects.equals(u, self) && !teamSet.contains(u)) {
						throw new IllegalArgumentException("不能查看该员工考勤数据");
					}
				}
				q.setUidIn(req);
			}
			else {
				q.setUidIn(List.of(self));
			}
			return q;
		}
		// type 0
		Set<Integer> members = new LinkedHashSet<>(attendanceGroupService.listNetworkMemberIds(viewerId));
		members.addAll(attendanceGroupService.listWhitelistMemberIds());
		members.add(self);
		List<Integer> req = CollUtil.isEmpty(userId) ? List.of() : userId.stream().filter(Objects::nonNull).toList();
		if (CollUtil.isNotEmpty(req)) {
			List<Integer> inter = req.stream().filter(members::contains).toList();
			q.setUidIn(inter.isEmpty() ? List.of(-1) : inter);
			return q;
		}
		if (scope != null) {
			// scope 与部门过滤：未完全对齐 PHP admin_info，此处仅处理 frame_id / group_id
			Set<Integer> pool = new LinkedHashSet<>(members);
			if (frameId != null && frameId > 0) {
				Set<Integer> fu = new HashSet<>(listUserIdsByFrameTree(frameId));
				pool.retainAll(fu);
			}
			if (groupId != null && groupId > 0) {
				List<Integer> gu = attendanceGroupService.listGroupMemberBriefs(groupId, "")
					.stream()
					.map(o -> o.getId().intValue())
					.toList();
				pool.retainAll(new HashSet<>(gu));
			}
			q.setUidIn(pool.isEmpty() ? List.of(-1) : new ArrayList<>(pool));
			return q;
		}
		q.setUidIn(List.of(self));
		if (frameId != null && frameId > 0) {
			q.setFrameId(frameId);
		}
		if (groupId != null && groupId > 0) {
			q.setGroupId(groupId);
		}
		return q;
	}

	private List<Integer> listUserIdsByFrameTree(int frameId) {
		Set<Integer> frames = new LinkedHashSet<>();
		frames.add(frameId);
		List<Integer> sub = frameMapper.selectSubtreeFrameIds(DEFAULT_ENT_ID, frameId);
		if (CollUtil.isNotEmpty(sub)) {
			frames.addAll(sub);
		}
		List<FrameAssist> assists = frameAssistMapper.selectList(Wrappers.lambdaQuery(FrameAssist.class)
			.eq(FrameAssist::getEntid, DEFAULT_ENT_ID)
			.in(FrameAssist::getFrameId, frames)
			.eq(FrameAssist::getIsMastart, 1));
		return assists.stream().map(a -> a.getUserId().intValue()).distinct().toList();
	}

	private void applyPersonnelStatus(AttendanceStatisticsSearchQuery q, List<Integer> personnelStatus) {
		if (CollUtil.isEmpty(personnelStatus)) {
			return;
		}
		Set<Integer> statusSet = new LinkedHashSet<>();
		Integer loc = null;
		for (Integer code : personnelStatus) {
			if (code == null) {
				continue;
			}
			if (AttendanceClockConstants.SAME_CLOCK.contains(code)) {
				statusSet.add(code);
			}
			if (code == 5) {
				statusSet.addAll(AttendanceClockConstants.ALL_LACK_CARD);
			}
			if (code == 6) {
				loc = AttendanceClockConstants.OFFICE_ABNORMAL;
			}
		}
		if (!statusSet.isEmpty()) {
			q.setStatusInShifts(new ArrayList<>(statusSet));
		}
		if (loc != null) {
			q.setLocationEqAnyShift(loc);
		}
	}

	private void fillAbnormalRepairFilter(AttendanceStatisticsSearchQuery q, AttendanceGroup group) {
		List<Integer> shiftStatus = new ArrayList<>();
		if (group == null) {
			shiftStatus.addAll(AttendanceClockConstants.ALL_LACK_CARD);
			shiftStatus.addAll(AttendanceClockConstants.LATE_AND_LEAVE_EARLY);
			q.setRepairStatusIn(shiftStatus);
			q.setRepairLocationStatus(AttendanceClockConstants.OFFICE_ABNORMAL);
			return;
		}
		List<Integer> types = parseRepairTypes(group.getRepairType());
		if (types.contains(5)) {
			q.setRepairLocationStatus(AttendanceClockConstants.OFFICE_ABNORMAL);
		}
		if (types.contains(1)) {
			shiftStatus.addAll(AttendanceClockConstants.ALL_LACK_CARD);
		}
		if (types.contains(2)) {
			shiftStatus.add(AttendanceClockConstants.LATE);
		}
		if (types.contains(3)) {
			shiftStatus.add(AttendanceClockConstants.EXTREME_LATE);
		}
		if (types.contains(4)) {
			shiftStatus.add(AttendanceClockConstants.LEAVE_EARLY);
		}
		if (!shiftStatus.isEmpty()) {
			q.setRepairStatusIn(shiftStatus.stream().distinct().toList());
		}
	}

	private boolean repairAllowed(AttendanceGroup group) {
		return group != null && ObjectUtil.equals(group.getRepairAllowed(), 1)
				&& StrUtil.isNotBlank(group.getRepairType());
	}

	private List<Integer> parseRepairTypes(String repairType) {
		if (StrUtil.isBlank(repairType)) {
			return List.of();
		}
		String t = repairType.trim();
		try {
			return objectMapper.readValue(t, new TypeReference<>() {
			});
		}
		catch (Exception e) {
			List<Integer> out = new ArrayList<>();
			for (String p : t.split(",")) {
				if (StrUtil.isNotBlank(p)) {
					try {
						out.add(Integer.parseInt(p.trim()));
					}
					catch (NumberFormatException ignored) {
					}
				}
			}
			return out;
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

	private Map<String, Object> entityToMap(AttendanceStatistics row) {
		Map<String, Object> m = new LinkedHashMap<>();
		m.put("id", row.getId());
		m.put("uid", row.getUid());
		m.put("frame_id", row.getFrameId());
		m.put("group_id", row.getGroupId());
		m.put("group", row.getGroup());
		m.put("shift_id", row.getShiftId());
		try {
			m.put("shift_data", objectMapper.readValue(row.getShiftData(), Object.class));
		}
		catch (Exception e) {
			m.put("shift_data", Map.of());
		}
		putShifts(m, row);
		m.put("required_work_hours", row.getRequiredWorkHours());
		m.put("actual_work_hours", row.getActualWorkHours());
		m.put("created_at", row.getCreatedAt());
		m.put("updated_at", row.getUpdatedAt());
		return m;
	}

	private void putShifts(Map<String, Object> m, AttendanceStatistics row) {
		for (int i = 0; i < 4; i++) {
			String p = SHIFT_FIELDS[i];
			m.put(p + "_shift_time", shiftTimeGet(row, i));
			m.put(p + "_shift_is_after", shiftIsAfterGet(row, i));
			m.put(p + "_shift_status", shiftStatusGet(row, i));
			m.put(p + "_shift_location_status", shiftLocationGet(row, i));
			m.put(p + "_shift_record_id", shiftRecordGet(row, i));
		}
	}

	private record ClockAdjust(LocalDateTime time, int isAfter) {
	}

	private ClockAdjust computeClockTimeForStatus(AttendanceStatistics info, int number, int status) {
		if (status == AttendanceClockConstants.LACK_CARD || status == AttendanceClockConstants.LATE_LACK_CARD
				|| status == AttendanceClockConstants.EARLY_LACK_CARD) {
			return new ClockAdjust(null, 0);
		}
		JsonNode shift = readShiftJson(info.getShiftData());
		JsonNode rules = shift.path("rules");
		JsonNode rule = number > 1 ? rules.path(1) : rules.path(0);
		if (rule.isMissingNode()) {
			return new ClockAdjust(null, 0);
		}
		boolean isWork = (number == 0 || number == 2);
		String hm = isWork ? rule.path("work_hours").asText("09:00") : rule.path("off_hours").asText("18:00");
		LocalDate day = info.getCreatedAt() == null ? LocalDate.now(AttendanceShiftRuleValidation.zone())
				: info.getCreatedAt().toLocalDate();
		LocalTime lt = parseHm(hm);
		LocalDateTime base = LocalDateTime.of(day, lt);
		int lateSec = rule.path("late").asInt(0);
		int extSec = rule.path("extreme_late").asInt(0);
		int earlySec = rule.path("early_leave").asInt(0);
		LocalDateTime t = base;
		if (status == AttendanceClockConstants.LATE) {
			t = base.plusSeconds(lateSec + 1L);
		}
		else if (status == AttendanceClockConstants.EXTREME_LATE) {
			t = base.plusSeconds(extSec + 1L);
		}
		else if (status == AttendanceClockConstants.LEAVE_EARLY) {
			t = base.minusSeconds(Math.max(earlySec - 1, 0));
		}
		String dayAfterKey = isWork ? "first_day_after" : "second_day_after";
		int isAfter = rule.path(dayAfterKey).asInt(0);
		if (isAfter == 1) {
			t = t.plusDays(1);
		}
		return new ClockAdjust(t, isAfter);
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

	private static int shiftStatusGet(AttendanceStatistics e, int i) {
		return switch (i) {
			case 0 -> n(e.getOneShiftStatus());
			case 1 -> n(e.getTwoShiftStatus());
			case 2 -> n(e.getThreeShiftStatus());
			case 3 -> n(e.getFourShiftStatus());
			default -> 0;
		};
	}

	private static void shiftStatusSet(AttendanceStatistics e, int i, int v) {
		switch (i) {
			case 0 -> e.setOneShiftStatus(v);
			case 1 -> e.setTwoShiftStatus(v);
			case 2 -> e.setThreeShiftStatus(v);
			case 3 -> e.setFourShiftStatus(v);
			default -> {
			}
		}
	}

	private static int shiftLocationGet(AttendanceStatistics e, int i) {
		return switch (i) {
			case 0 -> n(e.getOneShiftLocationStatus());
			case 1 -> n(e.getTwoShiftLocationStatus());
			case 2 -> n(e.getThreeShiftLocationStatus());
			case 3 -> n(e.getFourShiftLocationStatus());
			default -> 0;
		};
	}

	private static void shiftLocationSet(AttendanceStatistics e, int i, int v) {
		switch (i) {
			case 0 -> e.setOneShiftLocationStatus(v);
			case 1 -> e.setTwoShiftLocationStatus(v);
			case 2 -> e.setThreeShiftLocationStatus(v);
			case 3 -> e.setFourShiftLocationStatus(v);
			default -> {
			}
		}
	}

	private static LocalDateTime shiftTimeGet(AttendanceStatistics e, int i) {
		return switch (i) {
			case 0 -> e.getOneShiftTime();
			case 1 -> e.getTwoShiftTime();
			case 2 -> e.getThreeShiftTime();
			case 3 -> e.getFourShiftTime();
			default -> null;
		};
	}

	private static void shiftTimeSet(AttendanceStatistics e, int i, LocalDateTime t) {
		switch (i) {
			case 0 -> e.setOneShiftTime(t);
			case 1 -> e.setTwoShiftTime(t);
			case 2 -> e.setThreeShiftTime(t);
			case 3 -> e.setFourShiftTime(t);
			default -> {
			}
		}
	}

	private static int shiftIsAfterGet(AttendanceStatistics e, int i) {
		return switch (i) {
			case 0 -> n(e.getOneShiftIsAfter());
			case 1 -> n(e.getTwoShiftIsAfter());
			case 2 -> n(e.getThreeShiftIsAfter());
			case 3 -> n(e.getFourShiftIsAfter());
			default -> 0;
		};
	}

	private static void shiftIsAfterSet(AttendanceStatistics e, int i, int v) {
		switch (i) {
			case 0 -> e.setOneShiftIsAfter(v);
			case 1 -> e.setTwoShiftIsAfter(v);
			case 2 -> e.setThreeShiftIsAfter(v);
			case 3 -> e.setFourShiftIsAfter(v);
			default -> {
			}
		}
	}

	private static long shiftRecordGet(AttendanceStatistics e, int i) {
		Long v = switch (i) {
			case 0 -> e.getOneShiftRecordId();
			case 1 -> e.getTwoShiftRecordId();
			case 2 -> e.getThreeShiftRecordId();
			case 3 -> e.getFourShiftRecordId();
			default -> 0L;
		};
		return v == null ? 0L : v;
	}

	private static int n(Integer x) {
		return x == null ? 0 : x;
	}

	private String buildResultText(int beforeStatus, int afterStatus, int beforeLoc, int afterLoc) {
		if (beforeStatus == afterStatus && beforeLoc > 0 && beforeLoc == afterLoc) {
			throw new IllegalArgumentException("打卡状态不能更新异常");
		}
		return statusText(beforeStatus) + " > " + statusText(afterStatus);
	}

	private String statusText(int s) {
		return switch (s) {
			case 1 -> "正常";
			case 2 -> "迟到";
			case 3 -> "严重迟到";
			case 4 -> "早退";
			case 5, 6, 7 -> "缺卡";
			default -> "--";
		};
	}

	private String formatOneDecimal(BigDecimal v) {
		if (v == null) {
			return "0.0";
		}
		return v.setScale(1, RoundingMode.HALF_UP).toPlainString();
	}

	private static String fmtApplyHours(BigDecimal v) {
		if (v == null) {
			return "0";
		}
		return v.stripTrailingZeros().toPlainString();
	}

	private List<Map<String, Object>> listHolidayTypesMeta() {
		List<ApproveHolidayType> types = approveHolidayTypeService.list(Wrappers.lambdaQuery(ApproveHolidayType.class)
			.orderByAsc(ApproveHolidayType::getSort)
			.orderByDesc(ApproveHolidayType::getId));
		List<Map<String, Object>> out = new ArrayList<>();
		for (ApproveHolidayType t : types) {
			Map<String, Object> row = new LinkedHashMap<>();
			row.put("id", t.getId());
			row.put("name", t.getName());
			row.put("duration_type", t.getDurationType());
			out.add(row);
		}
		return out;
	}

	private List<Map<String, Object>> buildHolidayDataForUid(Integer uid, String monthYm) {
		if (uid == null || uid <= 0) {
			return List.of();
		}
		LocalDateTime start = monthStart(monthYm);
		LocalDateTime end = monthEnd(monthYm);
		List<ApproveHolidayType> types = approveHolidayTypeService.list(Wrappers.lambdaQuery(ApproveHolidayType.class)
			.orderByAsc(ApproveHolidayType::getSort)
			.orderByDesc(ApproveHolidayType::getId));
		List<AttendanceApplyRecord> leaves = attendanceApplyRecordMapper.selectLeaveRecordsInMonth(uid, start, end);
		Map<Long, BigDecimal> sumByType = new HashMap<>();
		for (AttendanceApplyRecord r : leaves) {
			Long hid = parseHolidayTypeId(r.getOthers());
			if (hid == null) {
				continue;
			}
			BigDecimal wh = r.getWorkHours() == null ? BigDecimal.ZERO : r.getWorkHours();
			sumByType.merge(hid, wh, BigDecimal::add);
		}
		List<Map<String, Object>> out = new ArrayList<>();
		for (ApproveHolidayType t : types) {
			Map<String, Object> row = new LinkedHashMap<>();
			row.put("id", t.getId());
			row.put("duration",
					formatHolidayDuration(sumByType.getOrDefault(t.getId(), BigDecimal.ZERO), t.getDurationType()));
			out.add(row);
		}
		return out;
	}

	private Long parseHolidayTypeId(String others) {
		if (StrUtil.isBlank(others)) {
			return null;
		}
		try {
			JsonNode n = objectMapper.readTree(others);
			JsonNode idNode = n.get("holiday_type_id");
			if (idNode == null || idNode.isNull()) {
				return null;
			}
			if (idNode.isIntegralNumber()) {
				return idNode.longValue();
			}
			String s = idNode.asText();
			if (StrUtil.isBlank(s)) {
				return null;
			}
			return Long.parseLong(s.trim());
		}
		catch (Exception e) {
			return null;
		}
	}

	private static String formatHolidayDuration(BigDecimal hours, Integer durationType) {
		if (hours == null) {
			return "0";
		}
		if (durationType != null && durationType == 0) {
			return hours.stripTrailingZeros().toPlainString();
		}
		return hours.stripTrailingZeros().toPlainString();
	}

	private Map<String, Object> listData(List<?> list, long count) {
		Map<String, Object> m = new LinkedHashMap<>();
		m.put("list", list);
		m.put("count", count);
		return m;
	}

}
