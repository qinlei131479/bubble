package com.bubblecloud.biz.oa.service.impl;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.constant.AttendanceGroupConstants;
import com.bubblecloud.biz.oa.mapper.AdminMapper;
import com.bubblecloud.biz.oa.mapper.AttendanceGroupMapper;
import com.bubblecloud.biz.oa.mapper.AttendanceGroupMemberMapper;
import com.bubblecloud.biz.oa.mapper.AttendanceGroupShiftMapper;
import com.bubblecloud.biz.oa.mapper.AttendanceWhitelistMapper;
import com.bubblecloud.biz.oa.mapper.FrameAssistMapper;
import com.bubblecloud.biz.oa.mapper.FrameMapper;
import com.bubblecloud.biz.oa.service.AttendanceGroupService;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.dto.attendance.AttendanceGroupStepFourDTO;
import com.bubblecloud.oa.api.dto.attendance.AttendanceGroupStepOneDTO;
import com.bubblecloud.oa.api.dto.attendance.AttendanceGroupStepThreeDTO;
import com.bubblecloud.oa.api.dto.attendance.AttendanceGroupStepTwoDTO;
import com.bubblecloud.oa.api.dto.attendance.AttendanceRepeatCheckDTO;
import com.bubblecloud.oa.api.dto.attendance.AttendanceWhitelistSetDTO;
import com.bubblecloud.oa.api.entity.Admin;
import com.bubblecloud.oa.api.entity.AttendanceGroup;
import com.bubblecloud.oa.api.entity.AttendanceGroupMember;
import com.bubblecloud.oa.api.entity.AttendanceGroupShift;
import com.bubblecloud.oa.api.entity.AttendanceWhitelist;
import com.bubblecloud.oa.api.entity.Frame;
import com.bubblecloud.oa.api.entity.FrameAssist;
import com.bubblecloud.oa.api.vo.ListCountVO;
import com.bubblecloud.oa.api.vo.attendance.AttendanceGroupDetailVO;
import com.bubblecloud.oa.api.vo.attendance.AttendanceGroupListItemVO;
import com.bubblecloud.oa.api.vo.attendance.AttendanceGroupSelectItemVO;
import com.bubblecloud.oa.api.vo.attendance.AttendanceRepeatConflictVO;
import com.bubblecloud.oa.api.vo.attendance.AttendanceWhitelistVO;
import com.bubblecloud.oa.api.vo.attendance.OaIdNameVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 考勤组实现（对齐 PHP {@code AttendanceGroupService} 主流程）。
 *
 * @author qinlei
 * @date 2026/4/2 16:00
 */
@Service
@RequiredArgsConstructor
public class AttendanceGroupServiceImpl extends UpServiceImpl<AttendanceGroupMapper, AttendanceGroup>
		implements AttendanceGroupService {

	private static final Long DEFAULT_ENT_ID = 1L;

	private static final Set<Integer> CARD_REPLACEMENT = Set.of(1, 2, 3, 4, 5);

	private final AttendanceGroupMemberMapper memberMapper;

	private final AttendanceGroupShiftMapper shiftMapper;

	private final AttendanceWhitelistMapper whitelistMapper;

	private final FrameMapper frameMapper;

	private final FrameAssistMapper frameAssistMapper;

	private final AdminMapper adminMapper;

	private final ObjectMapper objectMapper;

	private record MemberGroupsResult(Map<Integer, Integer> memberToGroup, List<Integer> memberIds) {
	}

	private record FilterGroupResult(Map<Integer, Integer> memberToGroup, List<Integer> intersectUserIds) {
	}

	@Override
	public ListCountVO<AttendanceGroupListItemVO> listPage(Pg<AttendanceGroup> pg, String name) {
		AttendanceGroup q = new AttendanceGroup();
		if (StrUtil.isNotBlank(name)) {
			q.setNameLike(name);
		}
		Page<AttendanceGroup> res = findPg(pg, q);
		List<Long> superAdmins = whitelistMapper
			.selectList(Wrappers.lambdaQuery(AttendanceWhitelist.class)
				.eq(AttendanceWhitelist::getType, AttendanceGroupConstants.WHITELIST_ADMIN))
			.stream()
			.map(w -> w.getUid().longValue())
			.collect(Collectors.toList());
		List<AttendanceGroupListItemVO> rows = new ArrayList<>();
		for (AttendanceGroup g : res.getRecords()) {
			rows.add(toListItem(g, superAdmins));
		}
		return ListCountVO.of(rows, res.getTotal());
	}

	private AttendanceGroupListItemVO toListItem(AttendanceGroup g, List<Long> superAdmins) {
		AttendanceGroupListItemVO vo = new AttendanceGroupListItemVO();
		vo.setId(g.getId());
		vo.setName(g.getName());
		vo.setType(g.getType());
		vo.setCreatedAt(g.getCreatedAt());
		vo.setShiftIds(listShiftIds(g.getId()));
		vo.setSuperAdmins(superAdmins);
		if (ObjectUtil.equals(g.getType(), 1)) {
			List<Integer> frameIds = listMemberInts(g.getId(), AttendanceGroupConstants.FRAME);
			vo.setMembers(loadFrameNames(frameIds));
		}
		else {
			List<Integer> mids = listMemberInts(g.getId(), AttendanceGroupConstants.MEMBER);
			vo.setMembers(loadAdminNames(mids));
		}
		List<Long> adminIds = new ArrayList<>();
		List<Integer> adm = listMemberInts(g.getId(), AttendanceGroupConstants.ADMIN);
		for (Integer a : adm) {
			adminIds.add(a.longValue());
		}
		if (ObjectUtil.isNotNull(g.getUid())) {
			long u = g.getUid().longValue();
			if (!adminIds.contains(u)) {
				adminIds.add(u);
			}
		}
		vo.setAdmins(adminIds.stream().distinct().collect(Collectors.toList()));
		return vo;
	}

	private List<Integer> listShiftIds(int groupId) {
		return shiftMapper
			.selectList(Wrappers.lambdaQuery(AttendanceGroupShift.class).eq(AttendanceGroupShift::getGroupId, groupId))
			.stream()
			.map(AttendanceGroupShift::getShiftId)
			.collect(Collectors.toList());
	}

	private List<Integer> listMemberInts(int groupId, int type) {
		return memberMapper
			.selectList(Wrappers.lambdaQuery(AttendanceGroupMember.class)
				.eq(AttendanceGroupMember::getGroupId, groupId)
				.eq(AttendanceGroupMember::getType, type))
			.stream()
			.map(AttendanceGroupMember::getMember)
			.collect(Collectors.toList());
	}

	private List<OaIdNameVO> loadFrameNames(List<Integer> frameIds) {
		if (CollUtil.isEmpty(frameIds)) {
			return Collections.emptyList();
		}
		List<Long> lids = frameIds.stream().map(Integer::longValue).collect(Collectors.toList());
		List<Frame> frames = frameMapper.selectList(Wrappers.lambdaQuery(Frame.class).in(Frame::getId, lids));
		return frames.stream().map(f -> new OaIdNameVO(f.getId(), f.getName())).collect(Collectors.toList());
	}

	private List<OaIdNameVO> loadAdminNames(List<Integer> adminIds) {
		if (CollUtil.isEmpty(adminIds)) {
			return Collections.emptyList();
		}
		List<Admin> list = adminMapper.selectList(Wrappers.lambdaQuery(Admin.class).in(Admin::getId, adminIds));
		return list.stream().map(a -> new OaIdNameVO(a.getId(), a.getName())).collect(Collectors.toList());
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateByStep(Long id, JsonNode body) {
		JsonNode payload = ObjectUtil.isNull(body) ? objectMapper.createObjectNode() : body.deepCopy();
		String step = "one";
		if (payload instanceof com.fasterxml.jackson.databind.node.ObjectNode obj) {
			if (obj.hasNonNull("step")) {
				step = obj.remove("step").asText("one");
			}
		}
		int gid = id.intValue();
		try {
			switch (step) {
				case "one" -> updateStepOne(gid, objectMapper.treeToValue(payload, AttendanceGroupStepOneDTO.class));
				case "two" -> updateStepTwo(gid, objectMapper.treeToValue(payload, AttendanceGroupStepTwoDTO.class));
				case "three" ->
					updateStepThree(gid, objectMapper.treeToValue(payload, AttendanceGroupStepThreeDTO.class));
				case "four" -> updateStepFour(gid, objectMapper.treeToValue(payload, AttendanceGroupStepFourDTO.class));
				default -> throw new IllegalArgumentException("step 参数无效");
			}
		}
		catch (JsonProcessingException e) {
			throw new IllegalArgumentException("请求体解析失败");
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Integer saveGroup(AttendanceGroupStepOneDTO dto, Long uid) {
		if (ObjectUtil.isNull(uid)) {
			throw new IllegalArgumentException("用户未登录");
		}
		checkGroup(dto.getType(), dto.getName(), dto.getMembers(), null, dto.getOtherFilters());
		AttendanceGroup g = new AttendanceGroup();
		g.setName(StrUtil.trim(dto.getName()));
		g.setType(dto.getType() == null ? 0 : dto.getType());
		g.setUid(uid.intValue());
		fillNewGroupDefaults(g);
		save(g);
		int gid = g.getId();
		if (CollUtil.isNotEmpty(dto.getOtherFilters())) {
			applyOtherFilters(dto, gid);
		}
		int memType = ObjectUtil.equals(dto.getType(), 1) ? AttendanceGroupConstants.FRAME
				: AttendanceGroupConstants.MEMBER;
		handleMember(dto.getMembers(), memType, gid);
		mergeFiltersFromGroupMember(dto.getType(), dto.getMembers(), gid, dto.getFilters());
		handleMember(dto.getAdmins(), AttendanceGroupConstants.ADMIN, gid);
		handleShift(gid, dto.getShifts());
		return gid;
	}

	private void fillNewGroupDefaults(AttendanceGroup g) {
		if (g.getAddress() == null) {
			g.setAddress("");
		}
		if (g.getLat() == null) {
			g.setLat("0");
		}
		if (g.getLng() == null) {
			g.setLng("0");
		}
		if (g.getEffectiveRange() == null) {
			g.setEffectiveRange(0);
		}
		if (g.getLocationName() == null) {
			g.setLocationName("");
		}
		if (g.getRepairAllowed() == null) {
			g.setRepairAllowed(0);
		}
		if (g.getRepairType() == null) {
			g.setRepairType("[]");
		}
		if (g.getIsLimitTime() == null) {
			g.setIsLimitTime(0);
		}
		if (g.getLimitTime() == null) {
			g.setLimitTime(0);
		}
		if (g.getIsLimitNumber() == null) {
			g.setIsLimitNumber(0);
		}
		if (g.getLimitNumber() == null) {
			g.setLimitNumber(0);
		}
		if (g.getIsPhoto() == null) {
			g.setIsPhoto(0);
		}
		if (g.getIsExternal() == null) {
			g.setIsExternal(0);
		}
		if (g.getIsExternalNote() == null) {
			g.setIsExternalNote(0);
		}
		if (g.getIsExternalPhoto() == null) {
			g.setIsExternalPhoto(0);
		}
	}

	private void checkGroup(Integer type, String name, List<Integer> members, Integer excludeId,
			List<Integer> otherFilters) {
		if (excludeId != null && excludeId > 0) {
			AttendanceGroup exist = getById(excludeId);
			if (exist == null) {
				throw new IllegalArgumentException("操作失败，记录不存在");
			}
		}
		if (StrUtil.isNotBlank(name)) {
			LambdaQueryWrapper<AttendanceGroup> w = Wrappers.lambdaQuery(AttendanceGroup.class)
				.eq(AttendanceGroup::getName, name.trim());
			if (excludeId != null && excludeId > 0) {
				w.ne(AttendanceGroup::getId, excludeId);
			}
			if (count(w) > 0) {
				throw new IllegalArgumentException("考勤组名称重复");
			}
		}
		if (CollUtil.isNotEmpty(members)) {
			if (type != null && type == 1) {
				checkFrameRepeat(members, excludeId == null ? 0 : excludeId);
			}
			else {
				checkMemberRepeat(members, excludeId == null ? 0 : excludeId, otherFilters);
			}
		}
	}

	private void checkMemberRepeat(List<Integer> members, int excludeId, List<Integer> otherFilters) {
		MemberGroupsResult r = computeMemberIdsWithGroups(excludeId > 0 ? excludeId : null);
		Set<Integer> pool = new HashSet<>(r.memberIds());
		if (excludeId > 0) {
			pool.removeAll(getMemberIdsById(excludeId, false));
		}
		if (CollUtil.isNotEmpty(otherFilters)) {
			pool.removeAll(otherFilters);
		}
		for (Integer m : members) {
			if (pool.contains(m)) {
				throw new IllegalArgumentException("考勤人员重复");
			}
		}
	}

	private void checkFrameRepeat(List<Integer> frameMembers, int excludeId) {
		Set<Long> occupied = new HashSet<>();
		List<AttendanceGroup> type1 = list(Wrappers.lambdaQuery(AttendanceGroup.class).eq(AttendanceGroup::getType, 1));
		for (AttendanceGroup g : type1) {
			if (excludeId > 0 && g.getId().equals(excludeId)) {
				continue;
			}
			for (Integer fid : listMemberInts(g.getId(), AttendanceGroupConstants.FRAME)) {
				occupied.addAll(collectDescendantFrameIds(DEFAULT_ENT_ID, fid.longValue()));
			}
		}
		Set<Long> excludeSet = new HashSet<>();
		if (excludeId > 0) {
			for (Integer fid : listMemberInts(excludeId, AttendanceGroupConstants.FRAME)) {
				excludeSet.addAll(collectDescendantFrameIds(DEFAULT_ENT_ID, fid.longValue()));
			}
		}
		Set<Long> candidate = new HashSet<>();
		for (Integer m : frameMembers) {
			candidate.addAll(collectDescendantFrameIds(DEFAULT_ENT_ID, m.longValue()));
		}
		for (Long c : candidate) {
			if (excludeSet.contains(c)) {
				continue;
			}
			if (occupied.contains(c)) {
				throw new IllegalArgumentException("考勤部门重复");
			}
		}
	}

	private MemberGroupsResult computeMemberIdsWithGroups(Integer excludeGroupId) {
		LambdaQueryWrapper<AttendanceGroup> qw = Wrappers.lambdaQuery(AttendanceGroup.class);
		if (excludeGroupId != null && excludeGroupId > 0) {
			qw.ne(AttendanceGroup::getId, excludeGroupId);
		}
		List<AttendanceGroup> groups = list(qw);
		Map<Integer, Integer> memberToGroup = new HashMap<>();
		for (AttendanceGroup g : groups) {
			List<Integer> mids = getMemberIdsById(g.getId(), true);
			for (Integer mid : mids) {
				memberToGroup.put(mid, g.getId());
			}
		}
		List<Integer> merged = new ArrayList<>(memberToGroup.keySet());
		List<Integer> uniq = merged.stream().distinct().collect(Collectors.toList());
		if (excludeGroupId != null && excludeGroupId > 0) {
			uniq = filterMemberIds(excludeGroupId, uniq);
		}
		return new MemberGroupsResult(memberToGroup, uniq);
	}

	private List<Integer> getMemberIdsById(Integer groupId, boolean filter) {
		AttendanceGroup info = getById(groupId);
		if (info == null) {
			return Collections.emptyList();
		}
		List<Integer> members;
		if (ObjectUtil.equals(info.getType(), 1)) {
			List<Integer> frameIds = listMemberInts(groupId, AttendanceGroupConstants.FRAME);
			Set<Long> allFrameIds = new HashSet<>();
			for (Integer fid : frameIds) {
				allFrameIds.addAll(collectDescendantFrameIds(DEFAULT_ENT_ID, fid.longValue()));
			}
			List<Integer> fInts = allFrameIds.stream().map(Long::intValue).collect(Collectors.toList());
			members = listActiveAdminIdsByFrameIds(DEFAULT_ENT_ID, fInts);
		}
		else {
			members = listMemberInts(groupId, AttendanceGroupConstants.MEMBER);
		}
		if (filter) {
			members = filterMemberIds(groupId, members);
		}
		return members;
	}

	private List<Integer> filterMemberIds(Integer groupId, List<Integer> members) {
		Set<Integer> exclude = new HashSet<>(getWhiteListMemberIds());
		exclude.addAll(listMemberInts(groupId, AttendanceGroupConstants.FILTER));
		return members.stream().filter(m -> !exclude.contains(m)).collect(Collectors.toList());
	}

	private List<Integer> getWhiteListMemberIds() {
		return whitelistMapper
			.selectList(Wrappers.lambdaQuery(AttendanceWhitelist.class)
				.eq(AttendanceWhitelist::getType, AttendanceGroupConstants.WHITELIST_MEMBER))
			.stream()
			.map(AttendanceWhitelist::getUid)
			.collect(Collectors.toList());
	}

	private List<Integer> listActiveAdminIdsByFrameIds(Long entId, List<Integer> frameIds) {
		if (CollUtil.isEmpty(frameIds)) {
			return Collections.emptyList();
		}
		List<FrameAssist> assists = frameAssistMapper.selectList(Wrappers.lambdaQuery(FrameAssist.class)
			.eq(FrameAssist::getEntid, entId)
			.in(FrameAssist::getFrameId, frameIds)
			.eq(FrameAssist::getIsMastart, 1));
		Set<Long> userIds = assists.stream().map(FrameAssist::getUserId).collect(Collectors.toSet());
		if (userIds.isEmpty()) {
			return Collections.emptyList();
		}
		return adminMapper
			.selectList(Wrappers.lambdaQuery(Admin.class).in(Admin::getId, userIds).eq(Admin::getStatus, 1))
			.stream()
			.map(a -> a.getId().intValue())
			.collect(Collectors.toList());
	}

	private List<Long> collectDescendantFrameIds(Long entId, Long rootFrameId) {
		List<Frame> all = frameMapper.selectList(Wrappers.lambdaQuery(Frame.class).eq(Frame::getEntid, entId));
		Map<Long, List<Long>> children = new HashMap<>();
		for (Frame f : all) {
			long pid = f.getPid() == null ? 0L : f.getPid();
			children.computeIfAbsent(pid, k -> new ArrayList<>()).add(f.getId());
		}
		List<Long> out = new ArrayList<>();
		Deque<Long> dq = new ArrayDeque<>();
		dq.add(rootFrameId);
		while (!dq.isEmpty()) {
			Long cur = dq.poll();
			out.add(cur);
			List<Long> ch = children.get(cur);
			if (ch != null) {
				dq.addAll(ch);
			}
		}
		return out;
	}

	private FilterGroupResult filterGroupMember(int type, List<Integer> members, Integer excludeGroupId) {
		List<Integer> currentMemberIds;
		if (type == 1) {
			Set<Integer> acc = new HashSet<>();
			for (Integer m : members == null ? Collections.<Integer>emptyList() : members) {
				List<Integer> frameInts = collectDescendantFrameIds(DEFAULT_ENT_ID, m.longValue()).stream()
					.map(Long::intValue)
					.collect(Collectors.toList());
				acc.addAll(listActiveAdminIdsByFrameIds(DEFAULT_ENT_ID, frameInts));
			}
			currentMemberIds = new ArrayList<>(acc);
		}
		else {
			currentMemberIds = new ArrayList<>(members == null ? Collections.emptyList() : members);
		}
		MemberGroupsResult r = computeMemberIdsWithGroups(excludeGroupId);
		List<Integer> allMemberIds = r.memberIds();
		List<Integer> userIds = new ArrayList<>();
		if (CollUtil.isNotEmpty(allMemberIds) && CollUtil.isNotEmpty(currentMemberIds)) {
			userIds = currentMemberIds.stream().filter(allMemberIds::contains).distinct().collect(Collectors.toList());
		}
		return new FilterGroupResult(r.memberToGroup(), userIds);
	}

	private void mergeFiltersFromGroupMember(Integer type, List<Integer> members, int groupId, List<Integer> filters) {
		FilterGroupResult fg = filterGroupMember(type == null ? 0 : type, members, groupId);
		List<Integer> merged = new ArrayList<>();
		if (CollUtil.isNotEmpty(filters)) {
			merged.addAll(filters);
		}
		merged.addAll(fg.intersectUserIds());
		handleMember(merged.stream().distinct().collect(Collectors.toList()), AttendanceGroupConstants.FILTER, groupId);
	}

	private void applyOtherFilters(AttendanceGroupStepOneDTO data, int excludeGroupId) {
		if (CollUtil.isEmpty(data.getOtherFilters())) {
			return;
		}
		FilterGroupResult fg = filterGroupMember(data.getType() == null ? 0 : data.getType(), data.getMembers(),
				excludeGroupId);
		for (Integer filter : data.getOtherFilters()) {
			Integer otherGid = fg.memberToGroup().get(filter);
			if (otherGid != null) {
				long cnt = memberMapper.selectCount(Wrappers.lambdaQuery(AttendanceGroupMember.class)
					.eq(AttendanceGroupMember::getGroupId, otherGid)
					.eq(AttendanceGroupMember::getMember, filter)
					.eq(AttendanceGroupMember::getType, AttendanceGroupConstants.FILTER));
				if (cnt == 0) {
					AttendanceGroupMember row = new AttendanceGroupMember();
					row.setGroupId(otherGid);
					row.setMember(filter);
					row.setType(AttendanceGroupConstants.FILTER);
					memberMapper.insert(row);
				}
			}
		}
	}

	private void handleMember(List<Integer> members, int type, int groupId) {
		List<Integer> input = members == null ? Collections.emptyList() : members;
		Set<Integer> uniq = new LinkedHashSet<>(input);
		List<AttendanceGroupMember> existing = memberMapper.selectList(Wrappers.lambdaQuery(AttendanceGroupMember.class)
			.eq(AttendanceGroupMember::getGroupId, groupId)
			.eq(AttendanceGroupMember::getType, type));
		Map<Integer, Integer> memberToRowId = new HashMap<>();
		for (AttendanceGroupMember e : existing) {
			memberToRowId.put(e.getMember(), e.getId());
		}
		for (Integer m : uniq) {
			if (memberToRowId.containsKey(m)) {
				memberToRowId.remove(m);
			}
			else {
				AttendanceGroupMember row = new AttendanceGroupMember();
				row.setGroupId(groupId);
				row.setMember(m);
				row.setType(type);
				memberMapper.insert(row);
			}
		}
		for (Integer id : memberToRowId.values()) {
			memberMapper.deleteById(id);
		}
	}

	private void handleShift(int groupId, List<Integer> shifts) {
		List<Integer> input = shifts == null ? Collections.emptyList() : shifts;
		Set<Integer> uniq = new LinkedHashSet<>(input);
		List<AttendanceGroupShift> exist = shiftMapper
			.selectList(Wrappers.lambdaQuery(AttendanceGroupShift.class).eq(AttendanceGroupShift::getGroupId, groupId));
		Map<String, Integer> keyToId = new HashMap<>();
		for (AttendanceGroupShift s : exist) {
			keyToId.put(groupId + "_" + s.getShiftId(), s.getId());
		}
		for (Integer shift : uniq) {
			String k = groupId + "_" + shift;
			if (keyToId.containsKey(k)) {
				keyToId.remove(k);
			}
			else {
				AttendanceGroupShift row = new AttendanceGroupShift();
				row.setGroupId(groupId);
				row.setShiftId(shift);
				shiftMapper.insert(row);
			}
		}
		for (Integer id : keyToId.values()) {
			shiftMapper.deleteById(id);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateStepOne(Integer id, AttendanceGroupStepOneDTO dto) {
		AttendanceGroup info = getById(id);
		if (info == null) {
			throw new IllegalArgumentException("操作失败，记录不存在");
		}
		checkGroup(dto.getType(), dto.getName(), dto.getMembers(), id, dto.getOtherFilters());
		Integer oldType = info.getType();
		info.setName(StrUtil.trim(dto.getName()));
		info.setType(dto.getType() == null ? 0 : dto.getType());
		updateById(info);
		if (!ObjectUtil.equals(oldType, dto.getType())) {
			memberMapper.delete(Wrappers.lambdaQuery(AttendanceGroupMember.class)
				.eq(AttendanceGroupMember::getGroupId, id)
				.eq(AttendanceGroupMember::getType, ObjectUtil.equals(oldType, 1) ? AttendanceGroupConstants.FRAME
						: AttendanceGroupConstants.MEMBER));
		}
		if (CollUtil.isNotEmpty(dto.getOtherFilters())) {
			applyOtherFilters(dto, id);
		}
		int memType = ObjectUtil.equals(dto.getType(), 1) ? AttendanceGroupConstants.FRAME
				: AttendanceGroupConstants.MEMBER;
		handleMember(dto.getMembers(), memType, id);
		mergeFiltersFromGroupMember(dto.getType(), dto.getMembers(), id, dto.getFilters());
		handleMember(dto.getAdmins(), AttendanceGroupConstants.ADMIN, id);
		handleShift(id, dto.getShifts());
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateStepTwo(Integer id, AttendanceGroupStepTwoDTO dto) {
		AttendanceGroup g = getById(id);
		if (g == null) {
			throw new IllegalArgumentException("操作失败，记录不存在");
		}
		g.setAddress(dto.getAddress());
		g.setLat(dto.getLat());
		g.setLng(dto.getLng());
		g.setEffectiveRange(dto.getEffectiveRange());
		g.setLocationName(dto.getLocationName());
		updateById(g);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateStepThree(Integer id, AttendanceGroupStepThreeDTO dto) {
		AttendanceGroup g = getById(id);
		if (g == null) {
			throw new IllegalArgumentException("操作失败，记录不存在");
		}
		if (dto.getRepairType() != null) {
			for (Integer t : dto.getRepairType()) {
				if (!CARD_REPLACEMENT.contains(t)) {
					throw new IllegalArgumentException("请选择正确的补卡类型");
				}
			}
		}
		g.setRepairAllowed(dto.getRepairAllowed());
		try {
			g.setRepairType(objectMapper
				.writeValueAsString(dto.getRepairType() == null ? Collections.emptyList() : dto.getRepairType()));
		}
		catch (Exception e) {
			throw new IllegalArgumentException("补卡类型序列化失败");
		}
		g.setIsLimitTime(dto.getIsLimitTime());
		g.setLimitTime(dto.getLimitTime());
		g.setIsLimitNumber(dto.getIsLimitNumber());
		g.setLimitNumber(dto.getLimitNumber());
		g.setIsPhoto(dto.getIsPhoto());
		g.setIsExternal(dto.getIsExternal());
		g.setIsExternalNote(dto.getIsExternalNote());
		g.setIsExternalPhoto(dto.getIsExternalPhoto());
		updateById(g);
	}

	@Override
	public void updateStepFour(Integer id, AttendanceGroupStepFourDTO dto) {
		AttendanceGroup g = getById(id);
		if (g == null) {
			throw new IllegalArgumentException("操作失败，记录不存在");
		}
		// 排班周期写入 RosterCycle 后在此对接；当前仅占位成功
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteGroup(Long id) {
		if (ObjectUtil.isNull(id)) {
			throw new IllegalArgumentException("id不能为空");
		}
		int gid = id.intValue();
		AttendanceGroup g = getById(gid);
		if (ObjectUtil.isNull(g)) {
			throw new IllegalArgumentException("操作失败，记录不存在");
		}
		removeById(gid);
		memberMapper
			.delete(Wrappers.lambdaQuery(AttendanceGroupMember.class).eq(AttendanceGroupMember::getGroupId, gid));
		shiftMapper.delete(Wrappers.lambdaQuery(AttendanceGroupShift.class).eq(AttendanceGroupShift::getGroupId, gid));
	}

	@Override
	public AttendanceGroupDetailVO getInfo(Long id) {
		if (ObjectUtil.isNull(id)) {
			throw new IllegalArgumentException("id不能为空");
		}
		int gid = id.intValue();
		AttendanceGroup g = getById(gid);
		if (ObjectUtil.isNull(g)) {
			throw new IllegalArgumentException("操作失败，记录不存在");
		}
		AttendanceGroupDetailVO vo = new AttendanceGroupDetailVO();
		vo.setGroup(g);
		vo.setShiftIds(listShiftIds(gid));
		vo.setRepairTypes(parseRepairTypes(g.getRepairType()));
		if (ObjectUtil.equals(g.getType(), 1)) {
			List<Integer> fids = listMemberInts(gid, AttendanceGroupConstants.FRAME);
			vo.setMembers(loadFrameNames(fids));
		}
		else {
			List<Integer> mids = listMemberInts(gid, AttendanceGroupConstants.MEMBER);
			vo.setMembers(loadAdminNames(mids));
		}
		vo.setFilters(loadAdminNames(listMemberInts(gid, AttendanceGroupConstants.FILTER)));
		vo.setAdmins(loadAdminNames(listMemberInts(gid, AttendanceGroupConstants.ADMIN)));
		return vo;
	}

	private List<Integer> parseRepairTypes(String json) {
		if (StrUtil.isBlank(json)) {
			return Collections.emptyList();
		}
		try {
			return objectMapper.readValue(json, new TypeReference<List<Integer>>() {
			});
		}
		catch (Exception e) {
			return Collections.emptyList();
		}
	}

	@Override
	public AttendanceWhitelistVO getWhitelist() {
		AttendanceWhitelistVO vo = new AttendanceWhitelistVO();
		List<OaIdNameVO> mem = whitelistMapper
			.selectList(Wrappers.lambdaQuery(AttendanceWhitelist.class)
				.eq(AttendanceWhitelist::getType, AttendanceGroupConstants.WHITELIST_MEMBER))
			.stream()
			.map(w -> new OaIdNameVO(w.getUid().longValue(), String.valueOf(w.getUid())))
			.collect(Collectors.toList());
		List<OaIdNameVO> adm = whitelistMapper
			.selectList(Wrappers.lambdaQuery(AttendanceWhitelist.class)
				.eq(AttendanceWhitelist::getType, AttendanceGroupConstants.WHITELIST_ADMIN))
			.stream()
			.map(w -> new OaIdNameVO(w.getUid().longValue(), String.valueOf(w.getUid())))
			.collect(Collectors.toList());
		vo.setMembers(mem);
		vo.setAdmins(adm);
		return vo;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void setWhitelist(AttendanceWhitelistSetDTO dto) {
		setWhiteByType(dto.getMembers(), AttendanceGroupConstants.WHITELIST_MEMBER);
		setWhiteByType(dto.getAdmins(), AttendanceGroupConstants.WHITELIST_ADMIN);
	}

	private void setWhiteByType(List<Integer> list, int type) {
		List<Integer> want = list == null ? Collections.emptyList() : list;
		List<AttendanceWhitelist> exist = whitelistMapper
			.selectList(Wrappers.lambdaQuery(AttendanceWhitelist.class).eq(AttendanceWhitelist::getType, type));
		Set<Integer> old = exist.stream().map(AttendanceWhitelist::getUid).collect(Collectors.toSet());
		Set<Integer> target = new HashSet<>(want);
		for (Integer u : old) {
			if (!target.contains(u)) {
				whitelistMapper.delete(Wrappers.lambdaQuery(AttendanceWhitelist.class)
					.eq(AttendanceWhitelist::getType, type)
					.eq(AttendanceWhitelist::getUid, u));
			}
		}
		for (Integer u : target) {
			if (!old.contains(u)) {
				Admin a = adminMapper.selectById(u.longValue());
				if (a == null) {
					continue;
				}
				AttendanceWhitelist w = new AttendanceWhitelist();
				w.setUid(u);
				w.setType(type);
				whitelistMapper.insert(w);
			}
		}
	}

	@Override
	public List<AttendanceRepeatConflictVO> memberRepeatCheck(AttendanceRepeatCheckDTO dto) {
		int filterId = dto.getId() == null ? 0 : dto.getId();
		int type = dto.getType() == null ? 0 : dto.getType();
		List<Integer> members = dto.getMembers() == null ? Collections.emptyList() : dto.getMembers();
		MemberGroupsResult r = computeMemberIdsWithGroups(filterId > 0 ? filterId : null);
		Set<Integer> occupied = new HashSet<>(r.memberIds());
		List<Integer> checkIds;
		if (type > 0) {
			Set<Integer> pool = new HashSet<>();
			for (Integer frameId : members) {
				pool.addAll(listActiveAdminIdsByFrameIds(DEFAULT_ENT_ID,
						collectDescendantFrameIds(DEFAULT_ENT_ID, frameId.longValue()).stream()
							.map(Long::intValue)
							.collect(Collectors.toList())));
			}
			checkIds = pool.stream().filter(occupied::contains).distinct().collect(Collectors.toList());
		}
		else {
			checkIds = members.stream().filter(occupied::contains).distinct().collect(Collectors.toList());
		}
		if (checkIds.isEmpty()) {
			return Collections.emptyList();
		}
		List<AttendanceRepeatConflictVO> out = new ArrayList<>();
		for (Integer mid : checkIds) {
			Admin a = adminMapper.selectById(mid.longValue());
			if (a == null || !ObjectUtil.equals(a.getStatus(), 1)) {
				continue;
			}
			AttendanceRepeatConflictVO row = new AttendanceRepeatConflictVO();
			row.setId(a.getId());
			row.setName(a.getName());
			row.setAvatar(a.getAvatar());
			row.setPhone(a.getPhone());
			Integer gid = r.memberToGroup().get(mid);
			if (gid != null) {
				AttendanceGroup ag = getById(gid);
				if (ag != null) {
					row.setGroup(new OaIdNameVO(ag.getId().longValue(), ag.getName()));
				}
			}
			out.add(row);
		}
		return out;
	}

	@Override
	public List<Admin> getUnAttendMember() {
		MemberGroupsResult r = computeMemberIdsWithGroups(null);
		Set<Integer> inGroup = new HashSet<>(r.memberIds());
		Set<Integer> white = new HashSet<>(getWhiteListMemberIds());
		List<Admin> all = adminMapper.selectList(Wrappers.lambdaQuery(Admin.class).eq(Admin::getStatus, 1));
		List<Admin> diff = new ArrayList<>();
		for (Admin a : all) {
			int id = a.getId().intValue();
			if (!white.contains(id) && !inGroup.contains(id)) {
				diff.add(a);
			}
		}
		return diff;
	}

	@Override
	public List<AttendanceGroupSelectItemVO> getSelectList() {
		return list(Wrappers.lambdaQuery(AttendanceGroup.class).orderByAsc(AttendanceGroup::getId)).stream()
			.map(g -> new AttendanceGroupSelectItemVO(g.getId(), g.getName()))
			.collect(Collectors.toList());
	}

	@Override
	public List<AttendanceRepeatConflictVO> getGroupMembersByType(Integer type, Integer filterId) {
		MemberGroupsResult r = computeMemberIdsWithGroups(null);
		Set<Integer> groupMemberIds = new HashSet<>(r.memberIds());
		if (filterId != null && filterId > 0) {
			Set<Integer> filterIds = new HashSet<>();
			if (type != null && type == AttendanceGroupConstants.FRAME) {
				for (Integer frameId : listMemberInts(filterId, AttendanceGroupConstants.FRAME)) {
					filterIds.addAll(collectDescendantFrameIds(DEFAULT_ENT_ID, frameId.longValue()).stream()
						.map(Long::intValue)
						.collect(Collectors.toSet()));
				}
			}
			else {
				filterIds.addAll(getMemberIdsById(filterId, false));
			}
			groupMemberIds.removeAll(filterIds);
		}
		if (groupMemberIds.isEmpty()) {
			return Collections.emptyList();
		}
		List<AttendanceRepeatConflictVO> out = new ArrayList<>();
		for (Integer mid : groupMemberIds) {
			Admin a = adminMapper.selectById(mid.longValue());
			if (a == null || !ObjectUtil.equals(a.getStatus(), 1)) {
				continue;
			}
			AttendanceRepeatConflictVO row = new AttendanceRepeatConflictVO();
			row.setId(a.getId());
			row.setName(a.getName());
			row.setAvatar(a.getAvatar());
			row.setPhone(a.getPhone());
			row.setGroup(findGroupByUid(mid));
			out.add(row);
		}
		return out;
	}

	private OaIdNameVO findGroupByUid(int uid) {
		List<AttendanceGroup> groups = list(Wrappers.lambdaQuery(AttendanceGroup.class));
		for (AttendanceGroup g : groups) {
			if (getMemberIdsById(g.getId(), true).contains(uid)) {
				return new OaIdNameVO(g.getId().longValue(), g.getName());
			}
		}
		return null;
	}

	@Override
	public List<OaIdNameVO> listGroupMemberBriefs(Integer groupId, String nameLike) {
		if (!groupExists(groupId)) {
			throw new IllegalArgumentException("操作失败，考勤组记录不存在");
		}
		List<Integer> ids = getMemberIdsById(groupId, true);
		if (CollUtil.isEmpty(ids)) {
			return List.of();
		}
		List<Long> idLong = ids.stream().map(Integer::longValue).toList();
		var qw = Wrappers.lambdaQuery(Admin.class).eq(Admin::getStatus, 1).in(Admin::getId, idLong);
		if (StrUtil.isNotBlank(nameLike)) {
			qw.like(Admin::getName, nameLike.trim());
		}
		return adminMapper.selectList(qw)
			.stream()
			.map(a -> new OaIdNameVO(a.getId(), StrUtil.nullToEmpty(a.getName())))
			.toList();
	}

	@Override
	public List<Integer> listShiftIdsByGroup(Integer groupId) {
		if (ObjectUtil.isNull(groupId) || groupId <= 0) {
			return List.of();
		}
		return listShiftIds(groupId);
	}

	@Override
	public boolean groupExists(Integer id) {
		return ObjectUtil.isNotNull(id) && id > 0 && getById(id) != null;
	}

	@Override
	public int countGroupsByIds(List<Integer> ids) {
		if (CollUtil.isEmpty(ids)) {
			return 0;
		}
		return (int) count(Wrappers.lambdaQuery(AttendanceGroup.class).in(AttendanceGroup::getId, ids));
	}

	@Override
	public List<Integer> listNetworkMemberIds(Long adminId) {
		if (adminId == null) {
			return List.of();
		}
		int self = adminId.intValue();
		LinkedHashSet<Integer> acc = new LinkedHashSet<>();
		acc.add(self);
		List<AttendanceGroup> groups = list();
		for (AttendanceGroup g : groups) {
			List<Integer> mem = getMemberIdsById(g.getId(), true);
			if (mem.contains(self)) {
				acc.addAll(mem);
			}
		}
		return new ArrayList<>(acc);
	}

	@Override
	public List<Integer> listWhitelistMemberIds() {
		return new ArrayList<>(getWhiteListMemberIds());
	}

	@Override
	public AttendanceGroup findFirstGroupForAdmin(Integer adminId) {
		if (adminId == null || adminId <= 0) {
			return null;
		}
		for (AttendanceGroup g : list()) {
			if (getMemberIdsById(g.getId(), true).contains(adminId)) {
				return g;
			}
		}
		return null;
	}

}
