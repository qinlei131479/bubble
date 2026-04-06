package com.bubblecloud.biz.oa.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.constant.ScheduleConstants;
import com.bubblecloud.biz.oa.service.CrmScheduleSideEffectService;
import com.bubblecloud.biz.oa.schedule.ScheduleOccurrenceHelper;
import com.bubblecloud.biz.oa.schedule.ScheduleOccurrenceHelper.Occurrence;
import com.bubblecloud.biz.oa.schedule.SchedulePeriodMutationHelper;
import com.bubblecloud.biz.oa.mapper.ScheduleRemindMapper;
import com.bubblecloud.biz.oa.mapper.ScheduleReplyMapper;
import com.bubblecloud.biz.oa.mapper.ScheduleTaskMapper;
import com.bubblecloud.biz.oa.mapper.ScheduleUserMapper;
import com.bubblecloud.biz.oa.mapper.UserScheduleMapper;
import com.bubblecloud.biz.oa.service.AdminService;
import com.bubblecloud.biz.oa.service.CalendarConfigService;
import com.bubblecloud.biz.oa.service.ScheduleApiService;
import com.bubblecloud.biz.oa.service.ScheduleTypeService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.dto.ScheduleDeleteDTO;
import com.bubblecloud.oa.api.dto.ScheduleIndexQueryDTO;
import com.bubblecloud.oa.api.dto.ScheduleReplySaveDTO;
import com.bubblecloud.oa.api.dto.ScheduleStatusUpdateDTO;
import com.bubblecloud.oa.api.dto.ScheduleStoreDTO;
import com.bubblecloud.oa.api.dto.ScheduleUpdateDTO;
import com.bubblecloud.oa.api.dto.UserScheduleQueryDTO;
import com.bubblecloud.oa.api.entity.Admin;
import com.bubblecloud.oa.api.entity.Schedule;
import com.bubblecloud.oa.api.entity.ScheduleRemind;
import com.bubblecloud.oa.api.entity.ScheduleReply;
import com.bubblecloud.oa.api.entity.ScheduleTask;
import com.bubblecloud.oa.api.entity.ScheduleType;
import com.bubblecloud.oa.api.entity.ScheduleUser;
import com.bubblecloud.oa.api.entity.UserSchedule;
import com.bubblecloud.oa.api.vo.schedule.ScheduleAdminBriefVO;
import com.bubblecloud.oa.api.vo.schedule.ScheduleCalendarCountItemVO;
import com.bubblecloud.oa.api.vo.schedule.ScheduleRecordVO;
import com.bubblecloud.oa.api.vo.schedule.ScheduleRemindCardVO;
import com.bubblecloud.oa.api.vo.schedule.ScheduleTypeCardVO;
import com.bubblecloud.oa.api.vo.schedule.UserScheduleDayWrapperVO;
import com.bubblecloud.oa.api.vo.schedule.UserScheduleItemVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.bubblecloud.biz.oa.mapper.ScheduleMapper;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;

/**
 * 日程核心实现：周期展开、日历计数与 type=0/1/2 修改删除（对齐 PHP 主流程）。
 *
 * @author qinlei
 * @date 2026/3/28 15:30
 */
@Service
@RequiredArgsConstructor
public class ScheduleApiServiceImpl extends UpServiceImpl<ScheduleMapper, Schedule> implements ScheduleApiService {

	private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	private static final DateTimeFormatter D = DateTimeFormatter.ISO_LOCAL_DATE;

	private final ScheduleUserMapper scheduleUserMapper;

	private final ScheduleRemindMapper scheduleRemindMapper;

	private final ScheduleTaskMapper scheduleTaskMapper;

	private final ScheduleReplyMapper scheduleReplyMapper;

	private final UserScheduleMapper userScheduleMapper;

	private final AdminService adminService;

	private final CalendarConfigService calendarConfigService;

	private final ScheduleTypeService scheduleTypeService;

	private final ObjectMapper objectMapper;

	private final ScheduleOccurrenceHelper occurrenceHelper;

	private final SchedulePeriodMutationHelper periodMutationHelper;

	private final CrmScheduleSideEffectService crmScheduleSideEffectService;

	@Override
	public List<ScheduleRecordVO> scheduleIndex(Long userId, ScheduleIndexQueryDTO body) {
		if (ObjectUtil.isNull(userId)) {
			return List.of();
		}
		Set<Long> ids = collectScheduleIds(userId);
		if (ids.isEmpty()) {
			return List.of();
		}
		var q = Wrappers.lambdaQuery(Schedule.class).in(Schedule::getId, ids);
		if (body.getCid() != null && !body.getCid().isEmpty()) {
			q.in(Schedule::getCid, body.getCid());
		}
		List<Schedule> rows = baseMapper.selectList(q);
		LocalDate rangeStart = parseDatePrefix(body.getStartTime());
		LocalDate rangeEnd = parseDatePrefix(body.getEndTime());
		if (ObjectUtil.isNull(rangeStart)) {
			return List.of();
		}
		if (ObjectUtil.isNull(rangeEnd)) {
			rangeEnd = rangeStart;
		}
		int periodView = ObjectUtil.defaultIfNull(body.getPeriod(), 1);
		List<LocalDate> calendarDays = occurrenceHelper.calendarDaysForPeriodView(periodView, rangeStart, rangeEnd);
		LinkedHashMap<String, ScheduleRecordVO> unique = new LinkedHashMap<>();
		for (Schedule s : rows) {
			if (ObjectUtil.isNull(s.getStartTime()) || ObjectUtil.isNull(s.getEndTime())) {
				continue;
			}
			if (!couldAppearInRange(s, rangeStart, rangeEnd)) {
				continue;
			}
			for (LocalDate day : calendarDays) {
				Occurrence occ = occurrenceHelper.haveSchedule(s, day, userId, this::computeFinish);
				if (occ.have()) {
					unique.putIfAbsent(occ.dedupeKey(), buildRecordVo(s, userId, occ.start(), occ.end(), occ.finish()));
				}
			}
		}
		return new ArrayList<>(unique.values());
	}

	private boolean couldAppearInRange(Schedule s, LocalDate rangeStart, LocalDate rangeEnd) {
		if (ObjectUtil.isNull(rangeStart) || ObjectUtil.isNull(rangeEnd)) {
			return true;
		}
		int period = ObjectUtil.defaultIfNull(s.getPeriod(), 0);
		if (period == ScheduleConstants.REPEAT_NOT) {
			LocalDate ds = s.getStartTime().toLocalDate();
			LocalDate de = s.getEndTime().toLocalDate();
			return !de.isBefore(rangeStart) && !ds.isAfter(rangeEnd);
		}
		if (ObjectUtil.isNotNull(s.getFailTime()) && s.getFailTime().toLocalDate().isBefore(rangeStart)) {
			return false;
		}
		return !s.getStartTime().toLocalDate().isAfter(rangeEnd);
	}

	private Set<Long> collectScheduleIds(Long userId) {
		Set<Long> ids = new HashSet<>();
		List<ScheduleUser> su = scheduleUserMapper
			.selectList(Wrappers.lambdaQuery(ScheduleUser.class).eq(ScheduleUser::getUid, userId));
		for (ScheduleUser u : su) {
			if (ObjectUtil.isNotNull(u.getScheduleId())) {
				ids.add(u.getScheduleId());
			}
		}
		List<Schedule> owned = baseMapper.selectList(Wrappers.lambdaQuery(Schedule.class).eq(Schedule::getUid, userId));
		for (Schedule s : owned) {
			ids.add(s.getId());
		}
		return ids;
	}

	private ScheduleRecordVO buildRecordVo(Schedule s, Long viewerId) {
		ScheduleRecordVO vo = new ScheduleRecordVO();
		vo.setId(s.getId());
		vo.setUid(s.getUid());
		vo.setCid(s.getCid());
		vo.setColor(StrUtil.nullToEmpty(s.getColor()));
		vo.setTitle(StrUtil.nullToEmpty(s.getTitle()));
		vo.setContent(StrUtil.nullToEmpty(s.getContent()));
		vo.setAllDay(ObjectUtil.defaultIfNull(s.getAllDay(), 0));
		vo.setStartTime(fmtDt(s.getStartTime()));
		vo.setEndTime(fmtDt(s.getEndTime()));
		vo.setPeriod(ObjectUtil.defaultIfNull(s.getPeriod(), 0));
		vo.setRate(ObjectUtil.defaultIfNull(s.getRate(), 1));
		vo.setDays(StrUtil.nullToEmpty(s.getDays()));
		vo.setLinkId(ObjectUtil.defaultIfNull(s.getLinkId(), 0L));
		vo.setFailTime(s.getFailTime() == null ? "" : fmtDt(s.getFailTime()));
		vo.setFinish(computeFinish(viewerId, s.getUid(), s.getId(), s.getStartTime(), s.getEndTime()));

		Admin master = adminService.getById(s.getUid());
		if (ObjectUtil.isNotNull(master)) {
			vo.setMaster(
					new ScheduleAdminBriefVO(master.getId(), master.getName(), master.getAvatar(), master.getPhone()));
		}
		ScheduleType t = scheduleTypeService.getById(s.getCid());
		if (ObjectUtil.isNotNull(t)) {
			vo.setType(new ScheduleTypeCardVO(t.getId(), t.getName(), t.getColor(), StrUtil.nullToEmpty(t.getInfo())));
		}
		List<ScheduleUser> users = scheduleUserMapper
			.selectList(Wrappers.lambdaQuery(ScheduleUser.class).eq(ScheduleUser::getScheduleId, s.getId()));
		for (ScheduleUser u : users) {
			Admin a = adminService.getById(u.getUid());
			if (ObjectUtil.isNotNull(a)) {
				vo.getUser().add(new ScheduleAdminBriefVO(a.getId(), a.getName(), a.getAvatar(), a.getPhone()));
			}
		}
		List<ScheduleRemind> reminds = scheduleRemindMapper.selectList(
				Wrappers.lambdaQuery(ScheduleRemind.class).eq(ScheduleRemind::getSid, s.getId()).last("LIMIT 1"));
		if (!reminds.isEmpty()) {
			ScheduleRemind r = reminds.get(0);
			vo.setRemind(new ScheduleRemindCardVO(StrUtil.nullToEmpty(r.getUniqued()), s.getId(),
					r.getRemindDay() == null ? "" : r.getRemindDay().toString(),
					r.getRemindTime() == null ? "" : r.getRemindTime().toString()));
		}
		return vo;
	}

	private ScheduleRecordVO buildRecordVo(Schedule s, Long viewerId, LocalDateTime occStart, LocalDateTime occEnd,
			int finish) {
		ScheduleRecordVO vo = buildRecordVo(s, viewerId);
		vo.setStartTime(fmtDt(occStart));
		vo.setEndTime(fmtDt(occEnd));
		vo.setFinish(finish);
		return vo;
	}

	private int computeFinish(Long viewerId, Long masterId, Long scheduleId, LocalDateTime st, LocalDateTime et) {
		ScheduleTask t = scheduleTaskMapper.selectOne(Wrappers.lambdaQuery(ScheduleTask.class)
			.eq(ScheduleTask::getPid, scheduleId)
			.eq(ScheduleTask::getUid, viewerId)
			.eq(ScheduleTask::getStartTime, st)
			.eq(ScheduleTask::getEndTime, et));
		if (ObjectUtil.isNotNull(t) && ObjectUtil.isNotNull(t.getStatus())) {
			return t.getStatus();
		}
		if (viewerId.equals(masterId)) {
			long c = scheduleUserMapper.selectCount(Wrappers.lambdaQuery(ScheduleUser.class)
				.eq(ScheduleUser::getScheduleId, scheduleId)
				.eq(ScheduleUser::getUid, viewerId));
			return c > 0 ? 1 : 3;
		}
		return -1;
	}

	private LocalDate parseDatePrefix(String s) {
		if (StrUtil.isBlank(s) || s.length() < 10) {
			return null;
		}
		try {
			return LocalDate.parse(s.substring(0, 10), D);
		}
		catch (DateTimeParseException e) {
			return null;
		}
	}

	private String fmtDt(LocalDateTime t) {
		return t == null ? "" : DT.format(t);
	}

	private LocalDateTime parseDateTime(String s) {
		if (StrUtil.isBlank(s)) {
			return null;
		}
		String x = s.trim();
		try {
			if (x.length() <= 10) {
				return LocalDate.parse(x.substring(0, 10), D).atStartOfDay();
			}
			return LocalDateTime.parse(x, DT);
		}
		catch (DateTimeParseException e) {
			return LocalDate.parse(x.substring(0, 10), D).atStartOfDay();
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveSchedule(Long userId, int entid, ScheduleStoreDTO dto) {
		validateStore(dto);
		persistInsertSchedule(userId, entid, dto);
	}

	private void persistInsertSchedule(Long userId, int entid, ScheduleStoreDTO dto) {
		ScheduleType ctype = scheduleTypeService.getById(dto.getCid());
		if (ObjectUtil.isNull(ctype)) {
			throw new IllegalArgumentException("请选择日程类型");
		}
		String color = StrUtil.isNotBlank(dto.getColor()) ? dto.getColor() : StrUtil.nullToEmpty(ctype.getColor());
		Schedule s = new Schedule();
		s.setUid(userId);
		s.setCid(dto.getCid());
		s.setColor(color);
		s.setTitle(StrUtil.nullToEmpty(dto.getTitle()));
		s.setContent(StrUtil.nullToEmpty(dto.getContent()));
		s.setAllDay(ObjectUtil.defaultIfNull(dto.getAllDay(), 0));
		s.setStartTime(parseDateTime(dto.getStartTime()));
		s.setEndTime(parseDateTime(dto.getEndTime()));
		s.setFailTime(parseDateTime(dto.getFailTime()));
		s.setLinkId(ObjectUtil.defaultIfNull(dto.getLinkId(), 0L));
		RemindBuild rb = buildRemindPayload(dto, s.getStartTime(), s.getEndTime());
		s.setPeriod(rb.period());
		s.setRate(rb.rate());
		s.setDays(rb.daysJson());
		s.setRemind(dto.getRemind() != null && dto.getRemind() >= 0 ? 1 : 0);
		s.setCreatedAt(LocalDateTime.now());
		s.setUpdatedAt(LocalDateTime.now());
		baseMapper.insert(s);
		Long sid = s.getId();
		List<Long> members = dto.getMember() == null ? List.of() : dto.getMember();
		for (Long m : members) {
			if (ObjectUtil.isNull(m)) {
				continue;
			}
			ScheduleUser su = new ScheduleUser();
			su.setUid(m);
			su.setScheduleId(sid);
			su.setIsMaster(0);
			scheduleUserMapper.insert(su);
			if (dto.getRemind() != null && dto.getRemind() >= 0) {
				insertRemind(entid, sid, m, s.getStartTime(), dto.getRemind(), dto.getRemindTime(), dto.getUniqued(),
						rb);
			}
		}
		long sc = ObjectUtil.defaultIfNull(dto.getCid(), 0L);
		if (sc == ScheduleConstants.TYPE_CLIENT_RENEW || sc == ScheduleConstants.TYPE_CLIENT_RETURN) {
			crmScheduleSideEffectService.syncRemindPeriodAfterScheduleInsert(StrUtil.nullToEmpty(dto.getUniqued()),
					dto.getStartTime(), dto.getEndTime());
		}
	}

	private void validateStore(ScheduleStoreDTO dto) {
		if (StrUtil.isBlank(dto.getTitle())) {
			throw new IllegalArgumentException("请填写日程标题");
		}
		if (ObjectUtil.isNull(dto.getCid()) || dto.getCid() <= 0) {
			throw new IllegalArgumentException("请选择日程类型");
		}
		if (ObjectUtil.isNull(dto.getPeriod())) {
			throw new IllegalArgumentException("请选择重复方式");
		}
		if (ObjectUtil.isNull(dto.getRate()) || dto.getRate() < 1) {
			throw new IllegalArgumentException("请填写重复频率");
		}
		if (StrUtil.isBlank(dto.getStartTime()) || StrUtil.isBlank(dto.getEndTime())) {
			throw new IllegalArgumentException("请选择日程开始/结束时间");
		}
	}

	private record RemindBuild(int period, int rate, String daysJson) {
	}

	private RemindBuild buildRemindPayload(ScheduleStoreDTO dto, LocalDateTime start, LocalDateTime end) {
		int period = ObjectUtil.defaultIfNull(dto.getPeriod(), 0);
		int rate = ObjectUtil.defaultIfNull(dto.getRate(), 1);
		String daysJson = "";
		if (period == ScheduleConstants.REPEAT_WEEK || period == ScheduleConstants.REPEAT_MONTH) {
			if (dto.getDays() == null || dto.getDays().isEmpty()) {
				throw new IllegalArgumentException("请选择提醒时间");
			}
			daysJson = objectMapper.valueToTree(dto.getDays()).toString();
		}
		else if (period == ScheduleConstants.REPEAT_DAY || period == ScheduleConstants.REPEAT_YEAR) {
			if (rate < 1) {
				throw new IllegalArgumentException("请选择重复频率");
			}
			daysJson = dto.getDays() == null ? "[]" : objectMapper.valueToTree(dto.getDays()).toString();
		}
		else {
			daysJson = dto.getDays() == null ? "[]" : objectMapper.valueToTree(dto.getDays()).toString();
		}
		return new RemindBuild(period, rate, daysJson);
	}

	private void insertRemind(int entid, Long sid, Long memberUid, LocalDateTime scheduleStart, Integer remind,
			String remindTimeStr, String uniqued, RemindBuild rb) {
		Schedule sch = baseMapper.selectById(sid);
		LocalDateTime remindAt;
		if (StrUtil.isNotBlank(remindTimeStr)) {
			remindAt = parseDateTime(remindTimeStr);
		}
		else {
			int code = ObjectUtil.defaultIfNull(remind, 0);
			remindAt = computeRemindAt(scheduleStart, code);
		}
		ScheduleRemind r = new ScheduleRemind();
		r.setSid(sid);
		r.setUid(String.valueOf(memberUid));
		r.setEntid(entid);
		r.setRemindDay(remindAt.toLocalDate());
		r.setRemindTime(remindAt.toLocalTime());
		r.setPeriod(rb.period());
		r.setRate(rb.rate());
		r.setDays(StrUtil.nullToEmpty(rb.daysJson()));
		r.setEndTime(sch.getFailTime() != null ? sch.getFailTime() : sch.getEndTime());
		r.setUniqued(
				StrUtil.isNotBlank(uniqued) ? uniqued : UUID.randomUUID().toString().replace("-", "").substring(0, 32));
		r.setCreatedAt(LocalDateTime.now());
		r.setUpdatedAt(LocalDateTime.now());
		scheduleRemindMapper.insert(r);
	}

	private LocalDateTime computeRemindAt(LocalDateTime start, int remindCode) {
		if (ObjectUtil.isNull(start)) {
			return LocalDateTime.now();
		}
		return switch (remindCode) {
			case 1 -> start.minusMinutes(5);
			case 2 -> start.minusMinutes(15);
			case 3 -> start.minusMinutes(30);
			case 4 -> start.minusHours(1);
			case 5 -> start.minusHours(2);
			case 6 -> start.minusDays(1);
			case 7 -> start.minusDays(2);
			case 8 -> start.minusWeeks(1);
			default -> start;
		};
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateSchedule(Long userId, int entid, Long id, ScheduleUpdateDTO dto) {
		if (ObjectUtil.isNull(id)) {
			throw new IllegalArgumentException("缺少日程ID");
		}
		Schedule existing = baseMapper.selectById(id);
		if (ObjectUtil.isNull(existing)) {
			throw new IllegalArgumentException("未找到相关记录");
		}
		int type = ObjectUtil.defaultIfNull(dto.getType(), ScheduleConstants.CHANGE_ALL);
		if (type == ScheduleConstants.CHANGE_NOW || type == ScheduleConstants.CHANGE_AFTER) {
			if (StrUtil.isBlank(dto.getStart()) || StrUtil.isBlank(dto.getEnd())) {
				throw new IllegalArgumentException("请传入当前实例的开始/结束时间（start、end）");
			}
		}
		ScheduleStoreDTO st = updateDtoToStore(dto);
		if (type == ScheduleConstants.CHANGE_ALL) {
			validateStore(st);
			persistUpdateScheduleRow(userId, entid, id, existing, st);
			return;
		}
		LocalDateTime instStart = parseDateTime(dto.getStart());
		LocalDateTime instEnd = parseDateTime(dto.getEnd());
		if (type == ScheduleConstants.CHANGE_AFTER) {
			if (!instStart.isAfter(existing.getStartTime())) {
				validateStore(st);
				persistUpdateScheduleRow(userId, entid, id, existing, st);
				return;
			}
			if (isRepeatingSeries(existing)) {
				validateStore(st);
				persistInsertSchedule(userId, entid, st);
			}
			LocalDateTime failPrev = periodMutationHelper.getPreviousPeriod(existing.getEndTime(), instEnd,
					ObjectUtil.defaultIfNull(existing.getPeriod(), 0), ObjectUtil.defaultIfNull(existing.getRate(), 1),
					existing.getDays());
			applyFailTruncate(id, failPrev);
			return;
		}
		if (type == ScheduleConstants.CHANGE_NOW) {
			if (instStart.equals(existing.getStartTime())) {
				LocalDateTime[] next = periodMutationHelper.getNextPeriod(instStart, instEnd,
						ObjectUtil.defaultIfNull(existing.getPeriod(), 0),
						ObjectUtil.defaultIfNull(existing.getRate(), 1), existing.getDays());
				LocalDateTime nextStart = next[0];
				LocalDateTime nextEnd = next[1];
				if (ObjectUtil.isNull(existing.getFailTime()) || !nextStart.isAfter(existing.getFailTime())) {
					ScheduleStoreDTO clone = buildCloneStoreDto(existing, memberIds(id), firstRemind(id), nextStart,
							nextEnd, existing.getFailTime(), dto.getUniqued(), existing.getLinkId());
					validateStore(clone);
					persistInsertSchedule(userId, entid, clone);
				}
				ScheduleStoreDTO tail = updateDtoToStore(dto);
				tail.setFailTime(dto.getEnd());
				validateStore(tail);
				persistUpdateScheduleRow(userId, entid, id, existing, tail);
				return;
			}
			if (!instStart.isAfter(existing.getStartTime())) {
				throw new IllegalArgumentException("实例开始时间与日程不匹配");
			}
			if (isRepeatingSeries(existing)) {
				ScheduleStoreDTO fork = updateDtoToStore(dto);
				LocalDateTime endDay = parseDateTime(dto.getEnd());
				if (ObjectUtil.isNotNull(endDay)) {
					fork.setFailTime(fmtDt(endDay.toLocalDate().atTime(LocalTime.MAX)));
				}
				validateStore(fork);
				persistInsertSchedule(userId, entid, fork);
			}
			LocalDateTime failPrev = periodMutationHelper.getPreviousPeriod(existing.getEndTime(), instEnd,
					ObjectUtil.defaultIfNull(existing.getPeriod(), 0), ObjectUtil.defaultIfNull(existing.getRate(), 1),
					existing.getDays());
			applyFailTruncate(id, failPrev);
			LocalDateTime[] next = periodMutationHelper.getNextPeriod(instStart, instEnd,
					ObjectUtil.defaultIfNull(existing.getPeriod(), 0), ObjectUtil.defaultIfNull(existing.getRate(), 1),
					existing.getDays());
			if (ObjectUtil.isNull(existing.getFailTime()) || !next[0].isAfter(existing.getFailTime())) {
				ScheduleRemind rem = firstRemind(id);
				String uq = rem == null ? "" : StrUtil.nullToEmpty(rem.getUniqued());
				ScheduleStoreDTO clone = buildCloneStoreDto(existing, memberIds(id), rem, next[0], next[1],
						existing.getFailTime(), uq, existing.getLinkId());
				validateStore(clone);
				persistInsertSchedule(userId, entid, clone);
			}
			return;
		}
		validateStore(st);
		persistUpdateScheduleRow(userId, entid, id, existing, st);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteSchedule(Long userId, Long id, ScheduleDeleteDTO dto) {
		if (ObjectUtil.isNull(id)) {
			throw new IllegalArgumentException("缺少日程ID");
		}
		Schedule existing = baseMapper.selectById(id);
		if (ObjectUtil.isNull(existing)) {
			throw new IllegalArgumentException("未找到相关记录");
		}
		int type = dto == null || dto.getType() == null ? ScheduleConstants.CHANGE_ALL : dto.getType();
		if (type == ScheduleConstants.CHANGE_ALL) {
			deleteScheduleFully(id, true);
			return;
		}
		if (dto == null || StrUtil.isBlank(dto.getStart()) || StrUtil.isBlank(dto.getEnd())) {
			throw new IllegalArgumentException("请传入当前实例的开始/结束时间（start、end）");
		}
		LocalDateTime instStart = parseDateTime(dto.getStart());
		LocalDateTime instEnd = parseDateTime(dto.getEnd());
		if (type == ScheduleConstants.CHANGE_AFTER) {
			if (!instStart.isAfter(existing.getStartTime()) && !instEnd.isAfter(existing.getEndTime())) {
				deleteScheduleFully(id, true);
				return;
			}
			if (instStart.isAfter(existing.getStartTime())) {
				if (isRepeatingSeries(existing)) {
					LocalDateTime failPrev = periodMutationHelper.getPreviousPeriod(existing.getEndTime(), instEnd,
							ObjectUtil.defaultIfNull(existing.getPeriod(), 0),
							ObjectUtil.defaultIfNull(existing.getRate(), 1), existing.getDays());
					applyFailTruncate(id, failPrev);
				}
				else {
					deleteScheduleFully(id, true);
				}
			}
			return;
		}
		if (type == ScheduleConstants.CHANGE_NOW) {
			if (instStart.equals(existing.getStartTime())) {
				if (!isRepeatingSeries(existing)) {
					deleteScheduleFully(id, true);
				}
				else {
					LocalDateTime[] next = periodMutationHelper.getNextPeriod(instStart, instEnd,
							ObjectUtil.defaultIfNull(existing.getPeriod(), 0),
							ObjectUtil.defaultIfNull(existing.getRate(), 1), existing.getDays());
					LocalDateTime failForRow = ObjectUtil.defaultIfNull(existing.getFailTime(),
							existing.getEndTime().plusYears(10));
					applyStartEndAndFailOnSchedule(id, next[0], next[1], failForRow);
				}
				return;
			}
			if (!instStart.isAfter(existing.getStartTime())) {
				throw new IllegalArgumentException("实例开始时间与日程不匹配");
			}
			LocalDateTime failPrev = periodMutationHelper.getPreviousPeriod(existing.getEndTime(), instEnd,
					ObjectUtil.defaultIfNull(existing.getPeriod(), 0), ObjectUtil.defaultIfNull(existing.getRate(), 1),
					existing.getDays());
			applyFailTruncate(id, failPrev);
			LocalDateTime[] next = periodMutationHelper.getNextPeriod(instStart, instEnd,
					ObjectUtil.defaultIfNull(existing.getPeriod(), 0), ObjectUtil.defaultIfNull(existing.getRate(), 1),
					existing.getDays());
			if (ObjectUtil.isNull(existing.getFailTime()) || !next[0].isAfter(existing.getFailTime())) {
				ScheduleRemind rem = firstRemind(id);
				String uq = rem == null ? "" : StrUtil.nullToEmpty(rem.getUniqued());
				ScheduleStoreDTO clone = buildCloneStoreDto(existing, memberIds(id), rem, next[0], next[1],
						existing.getFailTime(), uq, existing.getLinkId());
				validateStore(clone);
				persistInsertSchedule(userId, resolveEntidFromSchedule(id), clone);
			}
			return;
		}
		deleteScheduleFully(id, true);
	}

	private int resolveEntidFromSchedule(Long scheduleId) {
		ScheduleRemind r = firstRemind(scheduleId);
		if (ObjectUtil.isNotNull(r) && ObjectUtil.isNotNull(r.getEntid())) {
			return r.getEntid();
		}
		return 0;
	}

	private void deleteScheduleFully(Long scheduleId, boolean crmSideEffects) {
		Schedule sch = baseMapper.selectById(scheduleId);
		ScheduleRemind rem = firstRemind(scheduleId);
		String uniqued = rem == null ? "" : StrUtil.nullToEmpty(rem.getUniqued());
		Long cid = sch == null || sch.getCid() == null ? null : sch.getCid();
		scheduleRemindMapper.delete(Wrappers.lambdaQuery(ScheduleRemind.class).eq(ScheduleRemind::getSid, scheduleId));
		scheduleTaskMapper.delete(Wrappers.lambdaQuery(ScheduleTask.class).eq(ScheduleTask::getPid, scheduleId));
		scheduleUserMapper.delete(Wrappers.lambdaQuery(ScheduleUser.class).eq(ScheduleUser::getScheduleId, scheduleId));
		baseMapper.deleteById(scheduleId);
		if (crmSideEffects && StrUtil.isNotBlank(uniqued) && cid != null) {
			long c = cid.longValue();
			if (c == ScheduleConstants.TYPE_CLIENT_TRACK) {
				crmScheduleSideEffectService.afterClientTrackScheduleDeleted(uniqued);
			}
			else if (c == ScheduleConstants.TYPE_CLIENT_RENEW || c == ScheduleConstants.TYPE_CLIENT_RETURN) {
				crmScheduleSideEffectService.afterClientRemindScheduleDeleted(uniqued);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteRemindByUniqued(long uid, String uniqued) {
		if (StrUtil.isBlank(uniqued)) {
			return;
		}
		ScheduleRemind rem = scheduleRemindMapper.selectOne(Wrappers.lambdaQuery(ScheduleRemind.class)
			.eq(ScheduleRemind::getUniqued, uniqued)
			.eq(ScheduleRemind::getUid, String.valueOf(uid))
			.last("LIMIT 1"));
		if (rem == null) {
			rem = scheduleRemindMapper.selectOne(
					Wrappers.lambdaQuery(ScheduleRemind.class).eq(ScheduleRemind::getUniqued, uniqued).last("LIMIT 1"));
		}
		if (rem == null || ObjectUtil.isNull(rem.getSid())) {
			return;
		}
		deleteScheduleFully(rem.getSid(), false);
	}

	private void applyFailTruncate(Long scheduleId, LocalDateTime failBoundary) {
		if (ObjectUtil.isNull(failBoundary)) {
			return;
		}
		LocalDateTime endOfDay = failBoundary.toLocalDate().atTime(LocalTime.of(23, 59, 59));
		Schedule s = baseMapper.selectById(scheduleId);
		if (ObjectUtil.isNull(s)) {
			return;
		}
		s.setFailTime(endOfDay);
		s.setUpdatedAt(LocalDateTime.now());
		baseMapper.updateById(s);
		scheduleRemindMapper.update(null,
				Wrappers.lambdaUpdate(ScheduleRemind.class)
					.eq(ScheduleRemind::getSid, scheduleId)
					.set(ScheduleRemind::getEndTime, failBoundary));
	}

	private void applyStartEndAndFailOnSchedule(Long scheduleId, LocalDateTime start, LocalDateTime end,
			LocalDateTime failRaw) {
		Schedule s = baseMapper.selectById(scheduleId);
		if (ObjectUtil.isNull(s)) {
			return;
		}
		s.setStartTime(start);
		s.setEndTime(end);
		if (ObjectUtil.isNotNull(failRaw)) {
			s.setFailTime(failRaw.toLocalDate().atTime(LocalTime.of(23, 59, 59)));
		}
		s.setUpdatedAt(LocalDateTime.now());
		baseMapper.updateById(s);
		scheduleRemindMapper.update(null,
				Wrappers.lambdaUpdate(ScheduleRemind.class)
					.eq(ScheduleRemind::getSid, scheduleId)
					.set(ScheduleRemind::getEndTime, failRaw));
	}

	private boolean isRepeatingSeries(Schedule s) {
		int p = ObjectUtil.defaultIfNull(s.getPeriod(), 0);
		return p == ScheduleConstants.REPEAT_DAY || p == ScheduleConstants.REPEAT_WEEK
				|| p == ScheduleConstants.REPEAT_MONTH || p == ScheduleConstants.REPEAT_YEAR;
	}

	private List<Long> memberIds(Long scheduleId) {
		List<ScheduleUser> rows = scheduleUserMapper
			.selectList(Wrappers.lambdaQuery(ScheduleUser.class).eq(ScheduleUser::getScheduleId, scheduleId));
		List<Long> out = new ArrayList<>();
		for (ScheduleUser u : rows) {
			if (ObjectUtil.isNotNull(u.getUid())) {
				out.add(u.getUid());
			}
		}
		return out;
	}

	private ScheduleRemind firstRemind(Long sid) {
		List<ScheduleRemind> list = scheduleRemindMapper
			.selectList(Wrappers.lambdaQuery(ScheduleRemind.class).eq(ScheduleRemind::getSid, sid).last("LIMIT 1"));
		return list.isEmpty() ? null : list.get(0);
	}

	private ScheduleStoreDTO updateDtoToStore(ScheduleUpdateDTO dto) {
		ScheduleStoreDTO st = new ScheduleStoreDTO();
		st.setTitle(dto.getTitle());
		st.setMember(dto.getMember());
		st.setContent(dto.getContent());
		st.setCid(dto.getCid());
		st.setColor(dto.getColor());
		st.setRemind(dto.getRemind());
		st.setRemindTime(dto.getRemindTime());
		st.setRepeat(dto.getRepeat());
		st.setPeriod(dto.getPeriod());
		st.setRate(dto.getRate());
		st.setDays(dto.getDays());
		st.setAllDay(dto.getAllDay());
		st.setStartTime(dto.getStartTime());
		st.setEndTime(dto.getEndTime());
		st.setFailTime(dto.getFailTime());
		st.setUniqued(dto.getUniqued());
		st.setLinkId(dto.getLinkId());
		return st;
	}

	private ScheduleStoreDTO buildCloneStoreDto(Schedule info, List<Long> members, ScheduleRemind rem,
			LocalDateTime nextStart, LocalDateTime nextEnd, LocalDateTime seriesFail, String uniqued, Long linkId) {
		ScheduleStoreDTO d = new ScheduleStoreDTO();
		d.setTitle(info.getTitle());
		d.setContent(StrUtil.nullToEmpty(info.getContent()));
		d.setCid(info.getCid());
		d.setColor(StrUtil.nullToEmpty(info.getColor()));
		d.setAllDay(ObjectUtil.defaultIfNull(info.getAllDay(), 0));
		d.setStartTime(fmtDt(nextStart));
		d.setEndTime(fmtDt(nextEnd));
		d.setFailTime(seriesFail == null ? "" : fmtDt(seriesFail));
		d.setPeriod(info.getPeriod());
		d.setRate(info.getRate());
		List<Integer> dayInts = periodMutationHelper.parseDayInts(info.getDays());
		d.setDays(dayInts.isEmpty() ? new ArrayList<>() : new ArrayList<>(dayInts));
		if (ObjectUtil.defaultIfNull(info.getRemind(), 0) == 1 && ObjectUtil.isNotNull(rem)
				&& rem.getRemindDay() != null && rem.getRemindTime() != null) {
			d.setRemind(0);
			d.setRemindTime(fmtDt(LocalDateTime.of(rem.getRemindDay(), rem.getRemindTime())));
		}
		else {
			d.setRemind(-1);
		}
		d.setMember(members == null ? List.of() : members);
		d.setUniqued(StrUtil.nullToEmpty(uniqued));
		d.setLinkId(ObjectUtil.defaultIfNull(linkId, 0L));
		return d;
	}

	private void persistUpdateScheduleRow(Long userId, int entid, Long id, Schedule existing, ScheduleStoreDTO st) {
		scheduleRemindMapper.delete(Wrappers.lambdaQuery(ScheduleRemind.class).eq(ScheduleRemind::getSid, id));
		ScheduleType ctype = scheduleTypeService.getById(st.getCid());
		String color = StrUtil.isNotBlank(st.getColor()) ? st.getColor()
				: (ctype == null ? existing.getColor() : StrUtil.nullToEmpty(ctype.getColor()));
		RemindBuild rb = buildRemindPayload(st, parseDateTime(st.getStartTime()), parseDateTime(st.getEndTime()));
		existing.setCid(st.getCid());
		existing.setColor(color);
		existing.setTitle(StrUtil.nullToEmpty(st.getTitle()));
		existing.setContent(StrUtil.nullToEmpty(st.getContent()));
		existing.setAllDay(ObjectUtil.defaultIfNull(st.getAllDay(), 0));
		existing.setStartTime(parseDateTime(st.getStartTime()));
		existing.setEndTime(parseDateTime(st.getEndTime()));
		existing.setFailTime(parseDateTime(st.getFailTime()));
		existing.setPeriod(rb.period());
		existing.setRate(rb.rate());
		existing.setDays(rb.daysJson());
		existing.setRemind(st.getRemind() != null && st.getRemind() >= 0 ? 1 : 0);
		existing.setUpdatedAt(LocalDateTime.now());
		baseMapper.updateById(existing);
		List<Long> members = st.getMember() == null ? List.of() : st.getMember();
		scheduleUserMapper.delete(Wrappers.lambdaQuery(ScheduleUser.class).eq(ScheduleUser::getScheduleId, id));
		for (Long m : members) {
			if (ObjectUtil.isNull(m)) {
				continue;
			}
			ScheduleUser su = new ScheduleUser();
			su.setUid(m);
			su.setScheduleId(id);
			su.setIsMaster(0);
			scheduleUserMapper.insert(su);
			if (st.getRemind() != null && st.getRemind() >= 0) {
				insertRemind(entid, id, m, existing.getStartTime(), st.getRemind(), st.getRemindTime(), st.getUniqued(),
						rb);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateStatus(Long userId, ScheduleStatusUpdateDTO body) {
		if (ObjectUtil.isNull(body.getId())) {
			throw new IllegalArgumentException("缺少日程ID");
		}
		long c = scheduleUserMapper.selectCount(Wrappers.lambdaQuery(ScheduleUser.class)
			.eq(ScheduleUser::getScheduleId, body.getId())
			.eq(ScheduleUser::getUid, userId));
		if (c == 0) {
			throw new IllegalArgumentException("未找到相关记录");
		}
		LocalDateTime st = parseDateTime(body.getStart());
		LocalDateTime et = parseDateTime(body.getEnd());
		ScheduleTask probe = scheduleTaskMapper.selectOne(Wrappers.lambdaQuery(ScheduleTask.class)
			.eq(ScheduleTask::getPid, body.getId())
			.eq(ScheduleTask::getUid, userId)
			.eq(ScheduleTask::getStartTime, st)
			.eq(ScheduleTask::getEndTime, et));
		ScheduleTask save = new ScheduleTask();
		save.setPid(body.getId());
		save.setUid(userId);
		save.setStartTime(st);
		save.setEndTime(et);
		save.setStatus(ObjectUtil.defaultIfNull(body.getStatus(), 0));
		save.setUpdatedAt(LocalDateTime.now());
		if (ObjectUtil.isNotNull(probe)) {
			save.setId(probe.getId());
			save.setCreatedAt(probe.getCreatedAt());
			scheduleTaskMapper.updateById(save);
		}
		else {
			save.setCreatedAt(LocalDateTime.now());
			scheduleTaskMapper.insert(save);
		}
		Schedule sch = baseMapper.selectById(body.getId());
		if (ObjectUtil.isNotNull(sch) && ObjectUtil.isNotNull(sch.getCid())) {
			long scid = sch.getCid().longValue();
			if (scid == ScheduleConstants.TYPE_CLIENT_RENEW || scid == ScheduleConstants.TYPE_CLIENT_RETURN) {
				ScheduleRemind r = firstRemind(body.getId());
				if (ObjectUtil.isNotNull(r) && StrUtil.isNotBlank(r.getUniqued())) {
					crmScheduleSideEffectService.syncRemindPeriodAfterParticipantStatus(
							ObjectUtil.defaultIfNull(body.getStatus(), 0), r.getUniqued(), body.getStart(),
							body.getEnd(), ObjectUtil.defaultIfNull(sch.getPeriod(), 0),
							ObjectUtil.defaultIfNull(sch.getRate(), 1), StrUtil.nullToEmpty(sch.getDays()), scid);
				}
			}
		}
	}

	@Override
	public JsonNode scheduleInfo(Long userId, Long id, String startTime, String endTime) {
		boolean ok = baseMapper
			.selectCount(Wrappers.lambdaQuery(Schedule.class).eq(Schedule::getId, id).eq(Schedule::getUid, userId)) > 0
				|| scheduleUserMapper.selectCount(Wrappers.lambdaQuery(ScheduleUser.class)
					.eq(ScheduleUser::getScheduleId, id)
					.eq(ScheduleUser::getUid, userId)) > 0;
		if (!ok) {
			throw new IllegalArgumentException("未找到相关记录");
		}
		Schedule s = baseMapper.selectById(id);
		ObjectNode n = objectMapper.createObjectNode();
		n.put("id", s.getId());
		n.put("uid", ObjectUtil.defaultIfNull(s.getUid(), 0L));
		n.put("cid", ObjectUtil.defaultIfNull(s.getCid(), 0L));
		n.put("color", StrUtil.nullToEmpty(s.getColor()));
		n.put("title", StrUtil.nullToEmpty(s.getTitle()));
		n.put("content", StrUtil.nullToEmpty(s.getContent()));
		n.put("all_day", ObjectUtil.defaultIfNull(s.getAllDay(), 0));
		n.put("start_time", fmtDt(s.getStartTime()));
		n.put("end_time", fmtDt(s.getEndTime()));
		n.put("period", ObjectUtil.defaultIfNull(s.getPeriod(), 0));
		n.put("rate", ObjectUtil.defaultIfNull(s.getRate(), 1));
		n.put("days", StrUtil.nullToEmpty(s.getDays()));
		n.put("link_id", ObjectUtil.defaultIfNull(s.getLinkId(), 0L));
		n.put("fail_time", s.getFailTime() == null ? "" : fmtDt(s.getFailTime()));
		n.put("is_remind", ObjectUtil.defaultIfNull(s.getRemind(), 0));
		LocalDateTime st = parseDateTime(startTime);
		LocalDateTime et = parseDateTime(endTime);
		n.put("finish", (int) computeFinish(userId, s.getUid(), id, st, et));
		n.set("remindInfo", objectMapper.createObjectNode().put("ident", -1).put("text", "不提醒"));
		n.put("linkName", "");
		return n;
	}

	@Override
	public List<ScheduleCalendarCountItemVO> scheduleCount(Long userId, int entid, ScheduleIndexQueryDTO body) {
		LocalDate rangeStart = parseDatePrefix(body.getStartTime());
		LocalDate rangeEnd = parseDatePrefix(body.getEndTime());
		List<ScheduleCalendarCountItemVO> list = new ArrayList<>();
		if (ObjectUtil.isNull(userId) || ObjectUtil.isNull(rangeStart) || ObjectUtil.isNull(rangeEnd)) {
			return list;
		}
		Set<Long> ids = collectScheduleIds(userId);
		var q = Wrappers.lambdaQuery(Schedule.class).in(Schedule::getId, ids);
		if (body.getCid() != null && !body.getCid().isEmpty()) {
			q.in(Schedule::getCid, body.getCid());
		}
		List<Schedule> rows;
		if (ids.isEmpty()) {
			rows = List.of();
		}
		else {
			rows = baseMapper.selectList(q);
		}
		Map<String, Integer> countByDate = new HashMap<>();
		for (LocalDate d = rangeStart; !d.isAfter(rangeEnd); d = d.plusDays(1)) {
			countByDate.put(d.toString(), 0);
		}
		for (Schedule s : rows) {
			if (ObjectUtil.isNull(s.getStartTime()) || ObjectUtil.isNull(s.getEndTime())) {
				continue;
			}
			if (!couldAppearInRange(s, rangeStart, rangeEnd)) {
				continue;
			}
			for (LocalDate d = rangeStart; !d.isAfter(rangeEnd); d = d.plusDays(1)) {
				Occurrence occ = occurrenceHelper.haveSchedule(s, d, userId, this::computeFinish);
				if (occ.have()) {
					countByDate.merge(d.toString(), 1, Integer::sum);
				}
			}
		}
		String year = String.valueOf(rangeEnd.getYear());
		List<String> rests = calendarConfigService.getRestList(year);
		Set<String> restSet = new HashSet<>(rests);
		for (LocalDate cur = rangeStart; !cur.isAfter(rangeEnd); cur = cur.plusDays(1)) {
			int isRest = restSet.contains(cur.toString()) ? 1 : 0;
			int cnt = countByDate.getOrDefault(cur.toString(), 0);
			list.add(new ScheduleCalendarCountItemVO(cur.toString(), cnt, isRest));
		}
		return list;
	}

	@Override
	public JsonNode replys(Long scheduleId) {
		List<ScheduleReply> tops = scheduleReplyMapper.selectList(Wrappers.lambdaQuery(ScheduleReply.class)
			.eq(ScheduleReply::getPid, scheduleId)
			.and(w -> w.eq(ScheduleReply::getReplyId, 0L).or().isNull(ScheduleReply::getReplyId))
			.orderByAsc(ScheduleReply::getId));
		ArrayNode arr = objectMapper.createArrayNode();
		for (ScheduleReply r : tops) {
			arr.add(toReplyNode(r, scheduleId));
		}
		return arr;
	}

	private ObjectNode toReplyNode(ScheduleReply r, Long scheduleId) {
		ObjectNode n = objectMapper.createObjectNode();
		n.put("id", r.getId());
		n.put("uid", r.getUid());
		n.put("pid", r.getPid());
		n.put("reply_id", ObjectUtil.defaultIfNull(r.getReplyId(), 0L));
		n.put("to_uid", ObjectUtil.defaultIfNull(r.getToUid(), 0L));
		n.put("content", StrUtil.nullToEmpty(r.getContent()));
		n.put("created_at", r.getCreatedAt() == null ? "" : fmtDt(r.getCreatedAt()));
		Admin from = adminService.getById(r.getUid());
		if (from != null) {
			ObjectNode fu = objectMapper.createObjectNode();
			fu.put("id", from.getId());
			fu.put("name", StrUtil.nullToEmpty(from.getName()));
			fu.put("avatar", StrUtil.nullToEmpty(from.getAvatar()));
			n.set("from_user", fu);
		}
		List<ScheduleReply> children = scheduleReplyMapper.selectList(Wrappers.lambdaQuery(ScheduleReply.class)
			.eq(ScheduleReply::getPid, scheduleId)
			.eq(ScheduleReply::getReplyId, r.getId())
			.orderByAsc(ScheduleReply::getId));
		ArrayNode ch = objectMapper.createArrayNode();
		for (ScheduleReply c : children) {
			ch.add(toReplyChildNode(c));
		}
		n.set("children", ch);
		return n;
	}

	private ObjectNode toReplyChildNode(ScheduleReply r) {
		ObjectNode n = objectMapper.createObjectNode();
		n.put("id", r.getId());
		n.put("uid", r.getUid());
		n.put("pid", r.getPid());
		n.put("reply_id", ObjectUtil.defaultIfNull(r.getReplyId(), 0L));
		n.put("to_uid", ObjectUtil.defaultIfNull(r.getToUid(), 0L));
		n.put("content", StrUtil.nullToEmpty(r.getContent()));
		n.put("created_at", r.getCreatedAt() == null ? "" : fmtDt(r.getCreatedAt()));
		Admin from = adminService.getById(r.getUid());
		if (from != null) {
			ObjectNode fu = objectMapper.createObjectNode();
			fu.put("id", from.getId());
			fu.put("name", StrUtil.nullToEmpty(from.getName()));
			fu.put("avatar", StrUtil.nullToEmpty(from.getAvatar()));
			n.set("from_user", fu);
		}
		if (ObjectUtil.isNotNull(r.getToUid()) && r.getToUid() > 0) {
			Admin to = adminService.getById(r.getToUid());
			if (to != null) {
				ObjectNode tu = objectMapper.createObjectNode();
				tu.put("id", to.getId());
				tu.put("name", StrUtil.nullToEmpty(to.getName()));
				tu.put("avatar", StrUtil.nullToEmpty(to.getAvatar()));
				n.set("to_user", tu);
			}
		}
		return n;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveScheduleReply(Long userId, int entid, ScheduleReplySaveDTO dto) {
		if (ObjectUtil.isNull(dto.getPid()) || dto.getPid() <= 0) {
			throw new IllegalArgumentException("缺少日程ID");
		}
		ScheduleReply r = new ScheduleReply();
		r.setUid(userId);
		r.setPid(dto.getPid());
		r.setReplyId(ObjectUtil.defaultIfNull(dto.getReplyId(), 0L));
		r.setToUid(ObjectUtil.defaultIfNull(dto.getToUid(), 0L));
		r.setContent(StrUtil.nullToEmpty(dto.getContent()));
		r.setStartTime(parseDateTime(dto.getStartTime()));
		r.setEndTime(parseDateTime(dto.getEndTime()));
		r.setCreatedAt(LocalDateTime.now());
		r.setUpdatedAt(LocalDateTime.now());
		scheduleReplyMapper.insert(r);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteScheduleReply(Long userId, Long id) {
		ScheduleReply r = scheduleReplyMapper.selectById(id);
		if (ObjectUtil.isNull(r) || !userId.equals(r.getUid())) {
			throw new IllegalArgumentException("未找到可删除评价");
		}
		scheduleReplyMapper.deleteById(id);
		scheduleReplyMapper.delete(Wrappers.lambdaQuery(ScheduleReply.class).eq(ScheduleReply::getReplyId, id));
	}

	@Override
	public List<UserScheduleDayWrapperVO> userScheduleList(Long userId, UserScheduleQueryDTO query) {
		Admin a = adminService.getById(userId);
		UserScheduleDayWrapperVO w = new UserScheduleDayWrapperVO();
		if (ObjectUtil.isNull(a) || StrUtil.isBlank(a.getUid())) {
			return List.of(w);
		}
		LocalDate start = parseDatePrefix(query.getStart());
		LocalDate end = parseDatePrefix(query.getEnd());
		var q = Wrappers.lambdaQuery(UserSchedule.class).eq(UserSchedule::getUid, a.getUid());
		if (ObjectUtil.isNotNull(query.getEntid())) {
			q.eq(UserSchedule::getEntid, query.getEntid());
		}
		if (ObjectUtil.isNotNull(start) && ObjectUtil.isNotNull(end)) {
			q.ge(UserSchedule::getRemindDay, start).le(UserSchedule::getRemindDay, end);
		}
		List<UserSchedule> rows = userScheduleMapper.selectList(q);
		for (UserSchedule us : rows) {
			UserScheduleItemVO it = new UserScheduleItemVO();
			it.setId(us.getId());
			it.setTypes(us.getTypes());
			it.setContent(us.getContent());
			it.setMark(us.getMark());
			it.setRemind(us.getRemind());
			it.setRepeat(us.getRepeat());
			it.setPeriod(us.getPeriod());
			it.setRemindDay(us.getRemindDay() == null ? "" : us.getRemindDay().toString());
			it.setRemindTime(us.getRemindTime() == null ? "" : us.getRemindTime().toString());
			it.setEndTime(us.getEndTime() == null ? "" : fmtDt(us.getEndTime()));
			it.setLinkId(us.getLinkId());
			w.getList().add(it);
		}
		return List.of(w);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(Schedule req) {
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(Schedule req) {
		return super.update(req);
	}

}
