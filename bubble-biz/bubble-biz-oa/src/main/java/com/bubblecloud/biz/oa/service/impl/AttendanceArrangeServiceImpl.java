package com.bubblecloud.biz.oa.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.mapper.AttendanceArrangeMapper;
import com.bubblecloud.biz.oa.mapper.AttendanceArrangeRecordMapper;
import com.bubblecloud.biz.oa.service.AttendanceArrangeService;
import com.bubblecloud.biz.oa.service.AttendanceGroupService;
import com.bubblecloud.biz.oa.service.CalendarConfigService;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.dto.attendance.AttendanceArrangeBatchUpdateDTO;
import com.bubblecloud.oa.api.dto.attendance.AttendanceArrangeMonthInitDTO;
import com.bubblecloud.oa.api.dto.attendance.AttendanceArrangeUserShiftRowDTO;
import com.bubblecloud.oa.api.entity.AttendanceArrange;
import com.bubblecloud.oa.api.entity.AttendanceArrangeRecord;
import com.bubblecloud.oa.api.entity.AttendanceGroup;
import com.bubblecloud.oa.api.vo.ListCountVO;
import com.bubblecloud.oa.api.vo.attendance.AttendanceArrangeCalendarDayVO;
import com.bubblecloud.oa.api.vo.attendance.AttendanceArrangeGroupStubVO;
import com.bubblecloud.oa.api.vo.attendance.AttendanceArrangeInfoVO;
import com.bubblecloud.oa.api.vo.attendance.AttendanceArrangeListRowVO;
import com.bubblecloud.oa.api.vo.attendance.AttendanceArrangeUserMonthVO;
import com.bubblecloud.oa.api.vo.attendance.OaIdNameVO;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 考勤排班实现。
 *
 * @author qinlei
 * @date 2026/4/7 12:15
 */
@Service
@RequiredArgsConstructor
public class AttendanceArrangeServiceImpl extends UpServiceImpl<AttendanceArrangeMapper, AttendanceArrange>
		implements AttendanceArrangeService {

	private static final ZoneId TZ = ZoneId.of("Asia/Shanghai");

	private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	private final AttendanceArrangeRecordMapper arrangeRecordMapper;

	private final AttendanceGroupService attendanceGroupService;

	private final CalendarConfigService calendarConfigService;

	@Override
	public ListCountVO<AttendanceArrangeListRowVO> listPage(Pg<AttendanceArrange> pg, String name, String timeFilter) {
		AttendanceArrange q = new AttendanceArrange();
		q.setNameLike(StrUtil.trim(name));
		String ym = normalizeListMonth(timeFilter);
		q.setAttendMonth(ym);
		Page<AttendanceArrange> page = findPg(pg, q);
		List<AttendanceArrangeListRowVO> rows = new ArrayList<>();
		for (AttendanceArrange a : page.getRecords()) {
			rows.add(toListRow(a));
		}
		return ListCountVO.of(rows, page.getTotal());
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveMonth(Long uid, AttendanceArrangeMonthInitDTO dto) {
		if (ObjectUtil.isNull(uid)) {
			throw new IllegalArgumentException("用户未登录");
		}
		if (dto == null || StrUtil.isBlank(dto.getDate()) || CollUtil.isEmpty(dto.getGroups())) {
			throw new IllegalArgumentException("参数错误");
		}
		YearMonth ym = YearMonth.parse(dto.getDate().trim());
		if (ym.isBefore(YearMonth.now(TZ))) {
			throw new IllegalArgumentException("今日及以前的日期禁止调整");
		}
		if (attendanceGroupService.countGroupsByIds(dto.getGroups()) != dto.getGroups().size()) {
			throw new IllegalArgumentException("考勤组异常");
		}
		LocalDateTime anchor = ym.atDay(1).atStartOfDay();
		LocalDateTime now = LocalDateTime.now();
		for (Integer gid : dto.getGroups()) {
			long exists = count(Wrappers.lambdaQuery(AttendanceArrange.class)
				.eq(AttendanceArrange::getGroupId, gid)
				.apply("DATE_FORMAT(`date`,'%Y-%m') = {0}", ym.toString()));
			if (exists > 0) {
				continue;
			}
			AttendanceArrange a = new AttendanceArrange();
			a.setGroupId(gid);
			a.setUid(uid.intValue());
			a.setDate(anchor);
			a.setCreatedAt(now);
			a.setUpdatedAt(now);
			save(a);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateBatch(Integer groupId, Long uid, AttendanceArrangeBatchUpdateDTO body) {
		if (ObjectUtil.isNull(groupId) || groupId <= 0 || ObjectUtil.isNull(uid) || body == null) {
			throw new IllegalArgumentException("参数错误");
		}
		if (StrUtil.isBlank(body.getDate()) || CollUtil.isEmpty(body.getData())) {
			throw new IllegalArgumentException("参数错误");
		}
		YearMonth ym = YearMonth.parse(body.getDate().trim());
		if (ym.isBefore(YearMonth.now(TZ))) {
			throw new IllegalArgumentException("排班时间不能为过去月份");
		}
		if (!attendanceGroupService.groupExists(groupId)) {
			throw new IllegalArgumentException("操作失败，考勤组记录不存在");
		}
		List<Integer> shiftIds = attendanceGroupService.listShiftIdsByGroup(groupId);
		Set<Integer> allow = new HashSet<>(shiftIds);
		allow.add(0);
		allow.add(1);
		Set<Integer> memberSet = new HashSet<>(attendanceGroupService.listGroupMemberBriefs(groupId, "")
			.stream()
			.map(u -> u.getId().intValue())
			.toList());
		Long arrangeParentId = findOrCreateArrangeParent(groupId, ym, uid);
		LocalDate today = LocalDate.now(TZ);
		for (AttendanceArrangeUserShiftRowDTO item : body.getData()) {
			if (item.getUid() == null || CollUtil.isEmpty(item.getShifts())) {
				throw new IllegalArgumentException("排班数据异常");
			}
			if (item.getShifts().size() != ym.lengthOfMonth()) {
				throw new IllegalArgumentException("排班数据异常");
			}
			if (!memberSet.contains(item.getUid())) {
				throw new IllegalArgumentException("排班人员异常");
			}
			Map<String, Long> recordData = loadRecordIdMap(groupId, item.getUid(), ym);
			LocalDate cursor = ym.atDay(1);
			for (int key = 0; key < item.getShifts().size(); key++) {
				if (key > 0) {
					cursor = cursor.plusDays(1);
				}
				String dstr = cursor.toString();
				recordData.remove(dstr);
				if (!cursor.isAfter(today)) {
					continue;
				}
				int shift = item.getShifts().get(key);
				if (!allow.contains(shift)) {
					throw new IllegalArgumentException("排班班次异常");
				}
				upsertRecord(arrangeParentId, groupId, item.getUid(), shift, cursor);
			}
			for (Long rid : recordData.values()) {
				arrangeRecordMapper.deleteById(rid);
			}
		}
	}

	@Override
	public AttendanceArrangeInfoVO getInfo(Integer groupId, String name, String date) {
		if (ObjectUtil.isNull(groupId) || groupId <= 0) {
			throw new IllegalArgumentException("common.empty.attrs");
		}
		if (!attendanceGroupService.groupExists(groupId)) {
			throw new IllegalArgumentException("操作失败，考勤组记录不存在");
		}
		YearMonth ym = resolveYearMonth(date);
		YearMonth min = YearMonth.now(TZ).minusMonths(1);
		if (ym.isBefore(min)) {
			throw new IllegalArgumentException("考勤时间不能为过去月份");
		}
		List<String> rests = calendarConfigService.getRestList(ym.toString());
		Set<String> restSet = new HashSet<>(rests);
		List<AttendanceArrangeCalendarDayVO> calendar = new ArrayList<>();
		LocalDate d0 = ym.atDay(1);
		for (int i = 0; i < ym.lengthOfMonth(); i++) {
			LocalDate d = d0.plusDays(i);
			calendar.add(new AttendanceArrangeCalendarDayVO(d.toString(), restSet.contains(d.toString()) ? 1 : 0));
		}
		List<OaIdNameVO> members = attendanceGroupService.listGroupMemberBriefs(groupId, StrUtil.trim(name));
		Map<Integer, int[]> templates = new HashMap<>();
		for (OaIdNameVO m : members) {
			int[] arr = new int[ym.lengthOfMonth()];
			Map<String, Integer> got = loadShiftMap(groupId, m.getId().intValue(), ym);
			for (int i = 0; i < arr.length; i++) {
				LocalDate d = d0.plusDays(i);
				arr[i] = got.getOrDefault(d.toString(), 0);
			}
			templates.put(m.getId().intValue(), arr);
		}
		List<AttendanceArrangeUserMonthVO> arrange = new ArrayList<>();
		for (OaIdNameVO m : members) {
			AttendanceArrangeUserMonthVO row = new AttendanceArrangeUserMonthVO();
			row.setUid(m.getId().intValue());
			int[] arr = templates.get(m.getId().intValue());
			List<Integer> shifts = new ArrayList<>(arr.length);
			for (int v : arr) {
				shifts.add(v);
			}
			row.setShifts(shifts);
			arrange.add(row);
		}
		AttendanceArrangeInfoVO vo = new AttendanceArrangeInfoVO();
		vo.setArrange(arrange);
		vo.setCalendar(calendar);
		vo.setMembers(members);
		return vo;
	}

	private Map<String, Integer> loadShiftMap(Integer groupId, int uid, YearMonth ym) {
		List<AttendanceArrangeRecord> rows = arrangeRecordMapper
			.selectList(Wrappers.lambdaQuery(AttendanceArrangeRecord.class)
				.eq(AttendanceArrangeRecord::getGroupId, groupId)
				.eq(AttendanceArrangeRecord::getUid, uid)
				.apply("DATE_FORMAT(`date`,'%Y-%m') = {0}", ym.toString()));
		Map<String, Integer> m = new HashMap<>();
		for (AttendanceArrangeRecord r : rows) {
			if (r.getDate() != null) {
				m.put(r.getDate().toLocalDate().toString(), r.getShiftId());
			}
		}
		return m;
	}

	private Map<String, Long> loadRecordIdMap(Integer groupId, int uid, YearMonth ym) {
		List<AttendanceArrangeRecord> rows = arrangeRecordMapper
			.selectList(Wrappers.lambdaQuery(AttendanceArrangeRecord.class)
				.eq(AttendanceArrangeRecord::getGroupId, groupId)
				.eq(AttendanceArrangeRecord::getUid, uid)
				.apply("DATE_FORMAT(`date`,'%Y-%m') = {0}", ym.toString()));
		Map<String, Long> m = new HashMap<>();
		for (AttendanceArrangeRecord r : rows) {
			if (r.getDate() != null && r.getId() != null) {
				m.put(r.getDate().toLocalDate().toString(), r.getId());
			}
		}
		return m;
	}

	private Long findOrCreateArrangeParent(Integer groupId, YearMonth ym, Long uid) {
		AttendanceArrange one = getOne(Wrappers.lambdaQuery(AttendanceArrange.class)
			.eq(AttendanceArrange::getGroupId, groupId)
			.apply("DATE_FORMAT(`date`,'%Y-%m') = {0}", ym.toString())
			.last("LIMIT 1"));
		if (one != null) {
			return one.getId();
		}
		LocalDateTime now = LocalDateTime.now();
		AttendanceArrange a = new AttendanceArrange();
		a.setGroupId(groupId);
		a.setUid(uid.intValue());
		a.setDate(ym.atDay(1).atStartOfDay());
		a.setCreatedAt(now);
		a.setUpdatedAt(now);
		save(a);
		return a.getId();
	}

	private void upsertRecord(Long arrangeId, Integer groupId, Integer adminId, int shiftId, LocalDate day) {
		AttendanceArrangeRecord ex = arrangeRecordMapper.selectOne(Wrappers.lambdaQuery(AttendanceArrangeRecord.class)
			.eq(AttendanceArrangeRecord::getUid, adminId)
			.eq(AttendanceArrangeRecord::getGroupId, groupId)
			.apply("DATE(`date`) = {0}", day.toString())
			.last("LIMIT 1"));
		LocalDateTime ts = day.atStartOfDay();
		LocalDateTime now = LocalDateTime.now();
		if (ex != null) {
			ex.setArrangeId(arrangeId);
			ex.setShiftId(shiftId);
			ex.setUpdatedAt(now);
			arrangeRecordMapper.updateById(ex);
		}
		else {
			AttendanceArrangeRecord r = new AttendanceArrangeRecord();
			r.setArrangeId(arrangeId);
			r.setGroupId(groupId);
			r.setUid(adminId);
			r.setShiftId(shiftId);
			r.setDate(ts);
			r.setCreatedAt(now);
			r.setUpdatedAt(now);
			arrangeRecordMapper.insert(r);
		}
	}

	private AttendanceArrangeListRowVO toListRow(AttendanceArrange a) {
		AttendanceArrangeListRowVO v = new AttendanceArrangeListRowVO();
		v.setId(a.getId());
		v.setGroupId(a.getGroupId());
		v.setUid(a.getUid());
		v.setDate(a.getDate() == null ? "" : a.getDate().toLocalDate().toString());
		v.setCreatedAt(a.getCreatedAt() == null ? "" : DT.format(a.getCreatedAt()));
		v.setUpdatedAt(a.getUpdatedAt() == null ? "" : DT.format(a.getUpdatedAt()));
		AttendanceGroup g = attendanceGroupService.getById(a.getGroupId());
		if (g != null) {
			AttendanceArrangeGroupStubVO gs = new AttendanceArrangeGroupStubVO();
			gs.setId(g.getId());
			gs.setType(g.getType());
			gs.setName(g.getName());
			gs.setIsDelete(g.getDeletedAt() != null ? 1 : 0);
			try {
				gs.setMembers(attendanceGroupService.listGroupMemberBriefs(g.getId(), ""));
			}
			catch (IllegalArgumentException e) {
				gs.setMembers(List.of());
			}
			v.setGroup(gs);
		}
		return v;
	}

	private static YearMonth resolveYearMonth(String raw) {
		if (StrUtil.isBlank(raw)) {
			return YearMonth.now(TZ);
		}
		String s = raw.trim();
		if (s.length() >= 7 && s.charAt(4) == '-') {
			return YearMonth.parse(s.substring(0, 7));
		}
		return YearMonth.parse(s);
	}

	private static String normalizeListMonth(String timeFilter) {
		if (StrUtil.isBlank(timeFilter)) {
			return null;
		}
		String t = timeFilter.trim();
		if (t.contains("/")) {
			String[] parts = t.split("-");
			if (parts.length > 0) {
				String first = parts[0].replace('/', '-');
				if (first.length() >= 7) {
					return first.substring(0, 7);
				}
			}
			return null;
		}
		if (t.matches("\\d{4}-\\d{2}.*")) {
			return t.substring(0, 7);
		}
		return null;
	}

}
