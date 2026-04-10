package com.bubblecloud.biz.oa.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.AttendanceShiftMapper;
import com.bubblecloud.biz.oa.mapper.RosterCycleMapper;
import com.bubblecloud.biz.oa.mapper.RosterCycleShiftMapper;
import com.bubblecloud.biz.oa.service.AttendanceGroupService;
import com.bubblecloud.biz.oa.service.RosterCycleService;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.dto.attendance.RosterCycleSaveDTO;
import com.bubblecloud.oa.api.entity.AttendanceShift;
import com.bubblecloud.oa.api.entity.RosterCycle;
import com.bubblecloud.oa.api.entity.RosterCycleShift;
import com.bubblecloud.oa.api.vo.attendance.AttendanceShiftSelectItemVO;
import com.bubblecloud.oa.api.vo.attendance.RosterCycleListRowVO;
import com.fasterxml.jackson.databind.JsonNode;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 排班周期实现。
 *
 * @author qinlei
 * @date 2026/4/7 12:15
 */
@Service
@RequiredArgsConstructor
public class RosterCycleServiceImpl extends UpServiceImpl<RosterCycleMapper, RosterCycle>
		implements RosterCycleService {

	private final RosterCycleShiftMapper rosterCycleShiftMapper;

	private final AttendanceShiftMapper attendanceShiftMapper;

	private final AttendanceGroupService attendanceGroupService;

	@Override
	public List<RosterCycleListRowVO> listByGroup(Integer groupId) {
		if (ObjectUtil.isNull(groupId) || groupId <= 0) {
			throw new IllegalArgumentException("common.empty.attrs");
		}
		List<RosterCycle> rows = list(Wrappers.lambdaQuery(RosterCycle.class)
			.eq(RosterCycle::getGroupId, groupId)
			.isNull(RosterCycle::getDeletedAt)
			.orderByAsc(RosterCycle::getId));
		List<RosterCycleListRowVO> out = new ArrayList<>();
		for (RosterCycle c : rows) {
			out.add(toRow(c));
		}
		return out;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Integer saveCycle(Long uid, RosterCycleSaveDTO dto) {
		if (ObjectUtil.isNull(uid)) {
			throw new IllegalArgumentException("用户未登录");
		}
		if (dto.getGroupId() == null || dto.getGroupId() <= 0 || StrUtil.isBlank(dto.getName())
				|| dto.getCycle() == null || dto.getCycle() <= 0) {
			throw new IllegalArgumentException("参数错误");
		}
		List<Integer> shiftIds = parseShiftIds(dto.getShifts());
		assertCycleShifts(dto.getCycle(), dto.getGroupId(), shiftIds);
		LocalDateTime now = LocalDateTime.now();
		RosterCycle c = new RosterCycle();
		c.setGroupId(dto.getGroupId());
		c.setName(StrUtil.trim(dto.getName()));
		c.setCycle(dto.getCycle());
		c.setUid(uid.intValue());
		c.setCreatedAt(now);
		c.setUpdatedAt(now);
		save(c);
		int seq = 1;
		for (Integer sid : shiftIds) {
			RosterCycleShift link = new RosterCycleShift();
			link.setCycleId(c.getId());
			link.setShiftId(sid);
			link.setNumber(seq++);
			link.setUid(uid.intValue());
			link.setCreatedAt(now);
			link.setUpdatedAt(now);
			rosterCycleShiftMapper.insert(link);
		}
		return c.getId();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateCycle(Integer id, Long uid, RosterCycleSaveDTO dto) {
		if (ObjectUtil.isNull(id) || id <= 0 || ObjectUtil.isNull(uid)) {
			throw new IllegalArgumentException("common.empty.attrs");
		}
		if (StrUtil.isBlank(dto.getName()) || dto.getCycle() == null || dto.getCycle() <= 0) {
			throw new IllegalArgumentException("参数错误");
		}
		RosterCycle info = getById(id);
		if (info == null) {
			throw new IllegalArgumentException("操作失败，记录不存在");
		}
		List<Integer> shiftIds = parseShiftIds(dto.getShifts());
		assertCycleShifts(dto.getCycle(), info.getGroupId(), shiftIds);
		info.setName(StrUtil.trim(dto.getName()));
		info.setCycle(dto.getCycle());
		info.setUpdatedAt(LocalDateTime.now());
		updateById(info);
		rosterCycleShiftMapper.physicalDeleteByCycleId(id);
		LocalDateTime now = LocalDateTime.now();
		int seq = 1;
		for (Integer sid : shiftIds) {
			RosterCycleShift link = new RosterCycleShift();
			link.setCycleId(id);
			link.setShiftId(sid);
			link.setNumber(seq++);
			link.setUid(uid.intValue());
			link.setCreatedAt(now);
			link.setUpdatedAt(now);
			rosterCycleShiftMapper.insert(link);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteCycle(Integer id) {
		if (ObjectUtil.isNull(id) || id <= 0) {
			throw new IllegalArgumentException("common.empty.attrs");
		}
		if (getById(id) == null) {
			throw new IllegalArgumentException("操作失败，记录不存在");
		}
		rosterCycleShiftMapper.physicalDeleteByCycleId(id);
		baseMapper.physicalDeleteById(id);
	}

	@Override
	public RosterCycleListRowVO getDetail(Integer groupId, Integer id) {
		if (ObjectUtil.isNull(groupId) || groupId <= 0 || ObjectUtil.isNull(id) || id <= 0) {
			throw new IllegalArgumentException("common.empty.attrs");
		}
		RosterCycle c = getOne(Wrappers.lambdaQuery(RosterCycle.class)
			.eq(RosterCycle::getGroupId, groupId)
			.eq(RosterCycle::getId, id)
			.isNull(RosterCycle::getDeletedAt)
			.last("LIMIT 1"));
		if (c == null) {
			throw new IllegalArgumentException("操作失败，记录不存在");
		}
		return toRow(c);
	}

	@Override
	public List<RosterCycleListRowVO> listForArrange(Integer groupId) {
		return listByGroup(groupId);
	}

	private RosterCycleListRowVO toRow(RosterCycle c) {
		RosterCycleListRowVO vo = new RosterCycleListRowVO();
		vo.setId(c.getId());
		vo.setGroupId(c.getGroupId());
		vo.setName(c.getName());
		vo.setCycle(c.getCycle());
		List<RosterCycleShift> links = rosterCycleShiftMapper.selectList(Wrappers.lambdaQuery(RosterCycleShift.class)
			.eq(RosterCycleShift::getCycleId, c.getId())
			.isNull(RosterCycleShift::getDeletedAt)
			.orderByAsc(RosterCycleShift::getNumber));
		List<AttendanceShiftSelectItemVO> shifts = new ArrayList<>();
		for (RosterCycleShift l : links) {
			shifts.add(toShiftSelect(l.getShiftId()));
		}
		vo.setShifts(shifts);
		return vo;
	}

	private AttendanceShiftSelectItemVO toShiftSelect(Integer shiftId) {
		AttendanceShiftSelectItemVO v = new AttendanceShiftSelectItemVO();
		if (ObjectUtil.isNull(shiftId) || shiftId == 0) {
			v.setId(0);
			v.setName("");
			v.setColor("");
			return v;
		}
		AttendanceShift s = attendanceShiftMapper.selectById(shiftId);
		if (s == null) {
			v.setId(shiftId);
			v.setName("");
			v.setColor("");
			return v;
		}
		v.setId(s.getId());
		v.setName(StrUtil.nullToEmpty(s.getName()));
		v.setColor(StrUtil.nullToEmpty(s.getColor()));
		return v;
	}

	private static List<Integer> parseShiftIds(JsonNode shifts) {
		if (shifts == null || !shifts.isArray()) {
			throw new IllegalArgumentException("请设置周期班次");
		}
		List<Integer> out = new ArrayList<>();
		for (JsonNode n : shifts) {
			if (n.isNumber()) {
				out.add(n.intValue());
			}
			else if (n.isObject() && n.has("shift_id")) {
				out.add(n.get("shift_id").asInt());
			}
		}
		return out;
	}

	private void assertCycleShifts(int cycle, Integer groupId, List<Integer> shiftIds) {
		if (cycle != shiftIds.size()) {
			throw new IllegalArgumentException("排班周期班次异常");
		}
		List<Integer> groupShifts = attendanceGroupService.listShiftIdsByGroup(groupId);
		Set<Integer> allow = new HashSet<>(groupShifts);
		for (int sid : shiftIds) {
			if (sid != 0 && !allow.contains(sid)) {
				throw new IllegalArgumentException("考勤班次异常");
			}
		}
	}

}
