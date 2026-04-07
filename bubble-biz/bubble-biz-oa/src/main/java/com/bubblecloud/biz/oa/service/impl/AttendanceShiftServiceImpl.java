package com.bubblecloud.biz.oa.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.attendance.AttendanceShiftRuleValidation;
import com.bubblecloud.biz.oa.mapper.AttendanceGroupShiftMapper;
import com.bubblecloud.biz.oa.mapper.AttendanceArrangeRecordMapper;
import com.bubblecloud.biz.oa.mapper.AttendanceShiftMapper;
import com.bubblecloud.biz.oa.mapper.AttendanceShiftRuleMapper;
import com.bubblecloud.biz.oa.mapper.RosterCycleShiftMapper;
import com.bubblecloud.biz.oa.service.AdminService;
import com.bubblecloud.biz.oa.service.AttendanceShiftService;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.dto.attendance.AttendanceShiftSaveDTO;
import com.bubblecloud.oa.api.entity.Admin;
import com.bubblecloud.oa.api.entity.AttendanceGroupShift;
import com.bubblecloud.oa.api.entity.AttendanceShift;
import com.bubblecloud.oa.api.entity.AttendanceShiftRule;
import com.bubblecloud.oa.api.entity.RosterCycleShift;
import com.bubblecloud.oa.api.vo.ListCountVO;
import com.bubblecloud.oa.api.vo.attendance.AttendanceShiftAdminBriefVO;
import com.bubblecloud.oa.api.vo.attendance.AttendanceShiftDetailVO;
import com.bubblecloud.oa.api.vo.attendance.AttendanceShiftListRowVO;
import com.bubblecloud.oa.api.vo.attendance.AttendanceShiftRuleDetailVO;
import com.bubblecloud.oa.api.vo.attendance.AttendanceShiftSelectItemVO;
import com.bubblecloud.oa.api.vo.attendance.AttendanceShiftTimeSlotVO;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 考勤班次实现。
 *
 * @author qinlei
 * @date 2026/4/7 12:15
 */
@Service
@RequiredArgsConstructor
public class AttendanceShiftServiceImpl extends UpServiceImpl<AttendanceShiftMapper, AttendanceShift>
		implements AttendanceShiftService {

	private static final ZoneId TZ = AttendanceShiftRuleValidation.zone();

	private final AttendanceShiftRuleMapper shiftRuleMapper;

	private final AdminService adminService;

	private final AttendanceGroupShiftMapper attendanceGroupShiftMapper;

	private final RosterCycleShiftMapper rosterCycleShiftMapper;

	private final AttendanceArrangeRecordMapper attendanceArrangeRecordMapper;

	@Override
	public ListCountVO<AttendanceShiftListRowVO> listPage(Pg<AttendanceShift> pg, String name, Integer groupId) {
		AttendanceShift q = new AttendanceShift();
		q.setNameLike(StrUtil.trim(name));
		q.setGroupFilterId(groupId);
		Page<AttendanceShift> page = findPg(pg, q);
		List<AttendanceShiftListRowVO> rows = new ArrayList<>();
		for (AttendanceShift s : page.getRecords()) {
			rows.add(toListRow(s));
		}
		return ListCountVO.of(rows, page.getTotal());
	}

	@Override
	public List<AttendanceShiftSelectItemVO> selectList(String name, Integer groupId) {
		AttendanceShift q = new AttendanceShift();
		q.setNameLike(StrUtil.trim(name));
		q.setGroupFilterId(groupId);
		Page<AttendanceShift> p = new Page<>(1, Integer.MAX_VALUE);
		p.setSearchCount(false);
		List<AttendanceShift> list = baseMapper.findList(p, q);
		return list.stream().map(this::toSelectItem).collect(Collectors.toList());
	}

	private AttendanceShiftSelectItemVO toSelectItem(AttendanceShift s) {
		AttendanceShiftSelectItemVO v = new AttendanceShiftSelectItemVO();
		v.setId(s.getId());
		v.setName(s.getName());
		v.setColor(StrUtil.nullToEmpty(s.getColor()));
		return v;
	}

	private AttendanceShiftListRowVO toListRow(AttendanceShift s) {
		AttendanceShiftListRowVO vo = new AttendanceShiftListRowVO();
		vo.setId(s.getId());
		vo.setName(s.getName());
		vo.setUid(s.getUid());
		vo.setColor(StrUtil.nullToEmpty(s.getColor()));
		vo.setCreatedAt(s.getCreatedAt());
		vo.setUpdatedAt(s.getUpdatedAt());
		if (ObjectUtil.isNotNull(s.getUid())) {
			Admin a = adminService.getById(s.getUid().longValue());
			if (a != null) {
				vo.setCard(new AttendanceShiftAdminBriefVO(a.getId(), StrUtil.nullToEmpty(a.getName()),
						StrUtil.nullToEmpty(a.getAvatar()), StrUtil.nullToEmpty(a.getPhone())));
			}
		}
		List<AttendanceShiftRule> rules = shiftRuleMapper.selectList(Wrappers.lambdaQuery(AttendanceShiftRule.class)
			.eq(AttendanceShiftRule::getShiftId, s.getId())
			.orderByAsc(AttendanceShiftRule::getNumber));
		List<AttendanceShiftTimeSlotVO> times = new ArrayList<>();
		for (AttendanceShiftRule r : rules) {
			AttendanceShiftTimeSlotVO t = new AttendanceShiftTimeSlotVO();
			t.setWorkHours(StrUtil.nullToEmpty(r.getWorkHours()));
			t.setOffHours(StrUtil.nullToEmpty(r.getOffHours()));
			t.setShiftId(s.getId());
			t.setNumber(r.getNumber());
			t.setFirstDayAfter(r.getFirstDayAfter());
			t.setSecondDayAfter(r.getSecondDayAfter());
			times.add(t);
		}
		vo.setTimes(times);
		return vo;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Integer saveShift(Long uid, AttendanceShiftSaveDTO dto) {
		if (ObjectUtil.isNull(uid)) {
			throw new IllegalArgumentException("用户未登录");
		}
		validateNameDuplicate(null, dto.getName());
		List<AttendanceShiftRule> rules = AttendanceShiftRuleValidation.buildRules(dto);
		AttendanceShift s = new AttendanceShift();
		s.setUid(uid.intValue());
		s.setName(StrUtil.trim(dto.getName()));
		s.setNumber(ObjectUtil.defaultIfNull(dto.getNumber(), 1));
		s.setRestTime(ObjectUtil.defaultIfNull(dto.getRestTime(), 0));
		s.setRestStart(StrUtil.nullToEmpty(dto.getRestStart()));
		s.setRestEnd(StrUtil.nullToEmpty(dto.getRestEnd()));
		s.setOvertime(parseOvertime(dto.getOvertime()));
		s.setWorkTime(StrUtil.nullToEmpty(dto.getWorkTime()));
		s.setColor(StrUtil.nullToEmpty(dto.getColor()));
		s.setRestStartAfter(ObjectUtil.defaultIfNull(dto.getRestStartAfter(), 0));
		s.setRestEndAfter(ObjectUtil.defaultIfNull(dto.getRestEndAfter(), 0));
		s.setSort(0);
		LocalDateTime now = LocalDateTime.now();
		s.setCreatedAt(now);
		s.setUpdatedAt(now);
		save(s);
		for (AttendanceShiftRule r : rules) {
			r.setShiftId(s.getId());
			r.setCreatedAt(now);
			r.setUpdatedAt(now);
			shiftRuleMapper.insert(r);
		}
		return s.getId();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateShift(Integer id, AttendanceShiftSaveDTO dto) {
		if (ObjectUtil.isNull(id) || id <= 0) {
			throw new IllegalArgumentException("common.empty.attrs");
		}
		AttendanceShift existing = getById(id);
		if (existing == null) {
			throw new IllegalArgumentException("操作失败，记录不存在");
		}
		validateNameDuplicate(id, dto.getName());
		List<AttendanceShiftRule> rules = AttendanceShiftRuleValidation.buildRules(dto);
		existing.setName(StrUtil.trim(dto.getName()));
		existing.setNumber(ObjectUtil.defaultIfNull(dto.getNumber(), 1));
		existing.setRestTime(ObjectUtil.defaultIfNull(dto.getRestTime(), 0));
		existing.setRestStart(StrUtil.nullToEmpty(dto.getRestStart()));
		existing.setRestEnd(StrUtil.nullToEmpty(dto.getRestEnd()));
		existing.setOvertime(parseOvertime(dto.getOvertime()));
		existing.setWorkTime(StrUtil.nullToEmpty(dto.getWorkTime()));
		existing.setColor(StrUtil.nullToEmpty(dto.getColor()));
		existing.setRestStartAfter(ObjectUtil.defaultIfNull(dto.getRestStartAfter(), 0));
		existing.setRestEndAfter(ObjectUtil.defaultIfNull(dto.getRestEndAfter(), 0));
		existing.setUpdatedAt(LocalDateTime.now());
		updateById(existing);
		shiftRuleMapper.delete(Wrappers.lambdaQuery(AttendanceShiftRule.class).eq(AttendanceShiftRule::getShiftId, id));
		LocalDateTime now = LocalDateTime.now();
		for (AttendanceShiftRule r : rules) {
			r.setId(null);
			r.setShiftId(id);
			r.setCreatedAt(now);
			r.setUpdatedAt(now);
			shiftRuleMapper.insert(r);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteShift(Integer id) {
		if (ObjectUtil.isNull(id) || id <= 0) {
			throw new IllegalArgumentException("common.empty.attrs");
		}
		if (id <= 2) {
			throw new IllegalArgumentException("默认班次不能删除");
		}
		if (getById(id) == null) {
			throw new IllegalArgumentException("操作失败，记录不存在");
		}
		long g = attendanceGroupShiftMapper
			.selectCount(Wrappers.lambdaQuery(AttendanceGroupShift.class).eq(AttendanceGroupShift::getShiftId, id));
		if (g > 0) {
			throw new IllegalArgumentException("当前班次已被使用, 不能删除");
		}
		long rc = rosterCycleShiftMapper
			.selectCount(Wrappers.lambdaQuery(RosterCycleShift.class).eq(RosterCycleShift::getShiftId, id));
		if (rc > 0) {
			throw new IllegalArgumentException("当前班次已被使用, 不能删除");
		}
		String after = LocalDate.now(TZ).toString();
		attendanceArrangeRecordMapper.logicDeleteFutureByShiftId(id, after);
		shiftRuleMapper.delete(Wrappers.lambdaQuery(AttendanceShiftRule.class).eq(AttendanceShiftRule::getShiftId, id));
		removeById(id);
	}

	@Override
	public AttendanceShiftDetailVO getDetail(Integer id) {
		if (ObjectUtil.isNull(id) || id <= 0) {
			throw new IllegalArgumentException("common.empty.attrs");
		}
		AttendanceShift s = getById(id);
		if (s == null) {
			throw new IllegalArgumentException("操作失败，记录不存在");
		}
		List<AttendanceShiftRule> rules = shiftRuleMapper.selectList(Wrappers.lambdaQuery(AttendanceShiftRule.class)
			.eq(AttendanceShiftRule::getShiftId, id)
			.orderByAsc(AttendanceShiftRule::getNumber));
		AttendanceShiftDetailVO vo = new AttendanceShiftDetailVO();
		vo.setId(s.getId());
		vo.setName(s.getName());
		vo.setNumber(s.getNumber());
		vo.setRestTime(s.getRestTime());
		vo.setRestStart(StrUtil.nullToEmpty(s.getRestStart()));
		vo.setRestEnd(StrUtil.nullToEmpty(s.getRestEnd()));
		vo.setRestStartAfter(s.getRestStartAfter());
		vo.setRestEndAfter(s.getRestEndAfter());
		vo.setOvertime(s.getOvertime());
		vo.setWorkTime(StrUtil.nullToEmpty(s.getWorkTime()));
		vo.setColor(StrUtil.nullToEmpty(s.getColor()));
		vo.setUid(s.getUid());
		if (!rules.isEmpty()) {
			vo.setNumber1(toRuleDetail(rules.get(0)));
		}
		if (rules.size() > 1) {
			vo.setNumber2(toRuleDetail(rules.get(1)));
		}
		return vo;
	}

	private static AttendanceShiftRuleDetailVO toRuleDetail(AttendanceShiftRule r) {
		AttendanceShiftRuleDetailVO v = new AttendanceShiftRuleDetailVO();
		v.setId(r.getId());
		v.setShiftId(r.getShiftId());
		v.setNumber(r.getNumber());
		v.setFirstDayAfter(r.getFirstDayAfter());
		v.setSecondDayAfter(r.getSecondDayAfter());
		v.setWorkHours(StrUtil.nullToEmpty(r.getWorkHours()));
		v.setLate(r.getLate());
		v.setExtremeLate(r.getExtremeLate());
		v.setLateLackCard(r.getLateLackCard());
		v.setEarlyCard(r.getEarlyCard());
		v.setOffHours(StrUtil.nullToEmpty(r.getOffHours()));
		v.setEarlyLeave(r.getEarlyLeave());
		v.setEarlyLackCard(r.getEarlyLackCard());
		v.setDelayCard(r.getDelayCard());
		v.setFreeClock(r.getFreeClock());
		return v;
	}

	private void validateNameDuplicate(Integer excludeId, String name) {
		if (StrUtil.isBlank(name)) {
			throw new IllegalArgumentException("请填写班次名称");
		}
		var q = Wrappers.lambdaQuery(AttendanceShift.class).eq(AttendanceShift::getName, StrUtil.trim(name));
		if (ObjectUtil.isNotNull(excludeId) && excludeId > 0) {
			q.ne(AttendanceShift::getId, excludeId);
		}
		if (count(q) > 0) {
			throw new IllegalArgumentException("班次名称重复");
		}
	}

	private static int parseOvertime(String raw) {
		if (StrUtil.isBlank(raw)) {
			return 0;
		}
		try {
			return Integer.parseInt(raw.trim());
		}
		catch (NumberFormatException e) {
			return 0;
		}
	}

}
