package com.bubblecloud.biz.oa.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.FrameAssistMapper;
import com.bubblecloud.biz.oa.mapper.FrameMapper;
import com.bubblecloud.biz.oa.service.FrameService;
import com.bubblecloud.biz.oa.util.TreeUtil;
import com.bubblecloud.oa.api.dto.FrameSaveDTO;
import com.bubblecloud.oa.api.dto.FrameUpdateDTO;
import com.bubblecloud.oa.api.entity.Frame;
import com.bubblecloud.oa.api.vo.frame.FrameAdminBriefVO;
import com.bubblecloud.oa.api.vo.frame.FrameAssistUserRowVO;
import com.bubblecloud.oa.api.vo.frame.FrameAuthTreeNodeVO;
import com.bubblecloud.oa.api.vo.frame.FrameDepartmentTreeNodeVO;
import com.bubblecloud.oa.api.vo.frame.FrameDetailVO;
import com.bubblecloud.oa.api.vo.frame.FrameFormDataVO;
import com.bubblecloud.oa.api.vo.frame.FrameScopeItemVO;
import com.bubblecloud.oa.api.vo.frame.FrameSelectTreeNodeVO;
import com.bubblecloud.oa.api.vo.frame.FrameUserTreeNodeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 组织架构部门业务实现。
 *
 * @author qinlei
 * @date 2026/3/29 下午4:00
 */
@Service
@RequiredArgsConstructor
public class FrameServiceImpl implements FrameService {

	private final FrameMapper frameMapper;

	private final FrameAssistMapper frameAssistMapper;

	@Override
	public List<FrameDepartmentTreeNodeVO> departmentTreeList(int isShow, int entid) {
		List<Frame> rows = frameMapper.selectList(Wrappers.lambdaQuery(Frame.class)
			.eq(Frame::getEntid, (long) entid)
			.eq(Frame::getIsShow, isShow)
			.isNull(Frame::getDeletedAt)
			.orderByDesc(Frame::getLevel)
			.orderByDesc(Frame::getSort)
			.orderByAsc(Frame::getId));
		List<FrameDepartmentTreeNodeVO> flat = new ArrayList<>(rows.size());
		for (Frame f : rows) {
			FrameDepartmentTreeNodeVO n = new FrameDepartmentTreeNodeVO();
			n.setPid(f.getPid());
			n.setPath(f.getPath());
			n.setValue(f.getId());
			n.setLabel(f.getName());
			n.setUserCount(f.getUserCount());
			n.setUserSingleCount(f.getUserSingleCount());
			n.setIsCheck(false);
			flat.add(n);
		}
		return TreeUtil.buildTree(flat, FrameDepartmentTreeNodeVO::getValue, FrameDepartmentTreeNodeVO::getPid,
				FrameDepartmentTreeNodeVO::getChildren);
	}

	@Override
	public List<FrameAuthTreeNodeVO> getTree(Long userId, int entid, boolean withRole, boolean isScope) {
		List<Frame> rows = frameMapper.selectList(Wrappers.lambdaQuery(Frame.class)
			.eq(Frame::getEntid, (long) entid)
			.eq(Frame::getIsShow, 1)
			.isNull(Frame::getDeletedAt)
			.orderByAsc(Frame::getId));
		List<FrameAuthTreeNodeVO> flat = new ArrayList<>();
		for (Frame f : rows) {
			flat.add(toAuthNode(f, withRole));
		}
		if (!isScope) {
			return TreeUtil.buildTree(flat, FrameAuthTreeNodeVO::getId, FrameAuthTreeNodeVO::getPid,
					FrameAuthTreeNodeVO::getChildren);
		}
		List<FrameAuthTreeNodeVO> merged = new ArrayList<>();
		merged.add(roleAuthNode("self", "仅本人", "self"));
		merged.addAll(flat);
		return TreeUtil.buildTree(merged, FrameAuthTreeNodeVO::getId, FrameAuthTreeNodeVO::getPid,
				FrameAuthTreeNodeVO::getChildren);
	}

	@Override
	public List<FrameUserTreeNodeVO> getUserTree(Long userId, int entid, boolean withRole, boolean leave) {
		List<Frame> frames = frameMapper.selectList(Wrappers.lambdaQuery(Frame.class)
			.eq(Frame::getEntid, (long) entid)
			.eq(Frame::getIsShow, 1)
			.isNull(Frame::getDeletedAt)
			.orderByDesc(Frame::getSort)
			.orderByAsc(Frame::getId));
		List<FrameAssistUserRowVO> userRows = frameAssistMapper.selectUsersByEnt(entid);
		Map<Long, List<FrameAssistUserRowVO>> usersByFrame = userRows.stream()
			.filter(r -> r.getFrameId() != null)
			.collect(Collectors.groupingBy(FrameAssistUserRowVO::getFrameId));
		List<FrameUserTreeNodeVO> flat = new ArrayList<>();
		for (Frame f : frames) {
			List<FrameAssistUserRowVO> users = usersByFrame.getOrDefault(f.getId(), List.of());
			FrameUserTreeNodeVO node = new FrameUserTreeNodeVO();
			node.setId(f.getId());
			node.setPid(f.getPid());
			node.setEntid(f.getEntid());
			node.setUserCount(f.getUserCount());
			node.setPath(f.getPath());
			node.setValue(f.getId());
			node.setLabel(f.getName());
			node.setUserSingleCount(users.size());
			node.setType(0);
			node.setDisabled(false);
			node.setIsCheck(false);
			flat.add(node);
			for (FrameAssistUserRowVO u : users) {
				FrameUserTreeNodeVO um = new FrameUserTreeNodeVO();
				um.setId(u.getId() + "-" + f.getId());
				um.setPid(f.getId());
				um.setValue(u.getId());
				um.setLabel(u.getName());
				um.setName(u.getName());
				um.setAvatar(u.getAvatar());
				um.setPhone(u.getPhone());
				um.setJob(u.getJob());
				um.setUid(u.getUid());
				um.setType(1);
				um.setDisabled(false);
				flat.add(um);
			}
		}
		return TreeUtil.buildTree(flat, FrameUserTreeNodeVO::getId, FrameUserTreeNodeVO::getPid,
				FrameUserTreeNodeVO::getChildren);
	}

	@Override
	public FrameFormDataVO getFormData(int entid, long frameId) {
		FrameFormDataVO vo = new FrameFormDataVO();
		if (frameId > 0) {
			vo.setFrameInfo(departmentInfo(frameId, entid));
		}
		else {
			vo.setFrameInfo(new FrameDetailVO());
		}
		vo.setTree(treeForSelect(entid, frameId));
		return vo;
	}

	@Override
	public void createDepartment(FrameSaveDTO dto) {
		if (dto.getPath() == null || dto.getPath().isEmpty()) {
			throw new IllegalArgumentException("请选择上级部门");
		}
		List<Long> path = dto.getPath();
		int pid = path.get(path.size() - 1).intValue();
		if (pid <= 0) {
			throw new IllegalArgumentException("请选择上级部门");
		}
		int entid = dto.getEntid() == null ? 1 : dto.getEntid();
		Frame parent = frameMapper.selectOne(Wrappers.lambdaQuery(Frame.class)
			.eq(Frame::getId, (long) pid)
			.eq(Frame::getEntid, (long) entid)
			.isNull(Frame::getDeletedAt));
		if (parent == null) {
			throw new IllegalArgumentException("上级部门不存在");
		}
		String name = dto.getName() == null ? "" : dto.getName().trim();
		if (!StringUtils.hasText(name)) {
			throw new IllegalArgumentException("请填写部门名称");
		}
		long dup = frameMapper.selectCount(Wrappers.lambdaQuery(Frame.class)
			.eq(Frame::getEntid, (long) entid)
			.eq(Frame::getPid, pid)
			.eq(Frame::getName, name)
			.isNull(Frame::getDeletedAt));
		if (dup > 0) {
			throw new IllegalArgumentException("已存在相同部门，请勿重复创建");
		}
		int level = (parent.getLevel() == null ? 0 : parent.getLevel()) + 1;
		Frame f = new Frame();
		f.setUserId(0L);
		f.setEntid((long) entid);
		f.setPid(pid);
		f.setRoleId(dto.getRoleId() == null ? 0 : dto.getRoleId());
		f.setName(name);
		f.setIntroduce(dto.getIntroduce() == null ? "" : dto.getIntroduce());
		f.setSort(dto.getSort() == null ? 0 : dto.getSort());
		f.setUserCount(0);
		f.setUserSingleCount(0);
		f.setIsShow(1);
		f.setLevel(level);
		f.setPath("");
		LocalDateTime now = LocalDateTime.now();
		f.setCreatedAt(now);
		f.setUpdatedAt(now);
		frameMapper.insert(f);
		arrangePaths(entid);
	}

	@Override
	public void updateDepartment(long id, int entid, FrameUpdateDTO dto) {
		Frame existing = frameMapper.selectOne(Wrappers.lambdaQuery(Frame.class)
			.eq(Frame::getId, id)
			.eq(Frame::getEntid, (long) entid)
			.isNull(Frame::getDeletedAt));
		if (existing == null) {
			throw new IllegalArgumentException("未找到相关部门信息");
		}
		if (existing.getPid() == null || existing.getPid() == 0) {
			throw new IllegalArgumentException("修改该部门需要修改企业名称！");
		}
		int pid = existing.getPid();
		if (dto.getPath() != null && !dto.getPath().isEmpty()) {
			pid = dto.getPath().get(dto.getPath().size() - 1).intValue();
		}
		if (pid <= 0) {
			throw new IllegalArgumentException("请选择上级部门");
		}
		if (pid == id) {
			throw new IllegalArgumentException("本部门和上级部门不能相同");
		}
		String name = dto.getName() == null ? existing.getName() : dto.getName().trim();
		long dup = frameMapper.selectCount(Wrappers.lambdaQuery(Frame.class)
			.eq(Frame::getEntid, (long) entid)
			.eq(Frame::getPid, pid)
			.eq(Frame::getName, name)
			.ne(Frame::getId, id)
			.isNull(Frame::getDeletedAt));
		if (dup > 0) {
			throw new IllegalArgumentException("已存在相同部门，请勿重复创建");
		}
		Frame parent = frameMapper.selectOne(Wrappers.lambdaQuery(Frame.class)
			.eq(Frame::getId, (long) pid)
			.eq(Frame::getEntid, (long) entid)
			.isNull(Frame::getDeletedAt));
		if (parent == null) {
			throw new IllegalArgumentException("上级部门不存在");
		}
		int level = (parent.getLevel() == null ? 0 : parent.getLevel()) + 1;
		existing.setPid(pid);
		existing.setName(name);
		existing.setIntroduce(dto.getIntroduce() == null ? existing.getIntroduce() : dto.getIntroduce());
		existing.setSort(dto.getSort() == null ? existing.getSort() : dto.getSort());
		existing.setRoleId(dto.getRoleId() == null ? existing.getRoleId() : dto.getRoleId());
		existing.setLevel(level);
		existing.setUpdatedAt(LocalDateTime.now());
		frameMapper.updateById(existing);
		arrangePaths(entid);
	}

	@Override
	public FrameDetailVO departmentInfo(long id, int entid) {
		Frame f = frameMapper.selectOne(Wrappers.lambdaQuery(Frame.class)
			.eq(Frame::getId, id)
			.eq(Frame::getEntid, (long) entid)
			.isNull(Frame::getDeletedAt));
		if (f == null) {
			throw new IllegalArgumentException("未找到相关部门信息");
		}
		return toDetailVo(f);
	}

	@Override
	public void deleteDepartment(long id, int entid) {
		Frame f = frameMapper.selectOne(Wrappers.lambdaQuery(Frame.class)
			.eq(Frame::getId, id)
			.eq(Frame::getEntid, (long) entid)
			.isNull(Frame::getDeletedAt));
		if (f == null) {
			throw new IllegalArgumentException("未找到相关部门信息");
		}
		if (frameMapper.countByPid(id, (long) entid) > 0) {
			throw new IllegalArgumentException("删除失败，存在下级部门");
		}
		frameMapper.deleteById(id);
	}

	@Override
	public List<FrameAdminBriefVO> getFrameUsers(int frameId, int entid) {
		return frameAssistMapper.selectFrameUsers(frameId, entid);
	}

	@Override
	public List<FrameScopeItemVO> scopeFrames(Long userId, int entid) {
		List<Frame> rows = frameMapper.selectList(Wrappers.lambdaQuery(Frame.class)
			.eq(Frame::getEntid, (long) entid)
			.eq(Frame::getIsShow, 1)
			.isNull(Frame::getDeletedAt)
			.select(Frame::getId, Frame::getName));
		List<FrameScopeItemVO> list = new ArrayList<>();
		for (Frame f : rows) {
			list.add(new FrameScopeItemVO(f.getId(), f.getName()));
		}
		return list;
	}

	private List<FrameSelectTreeNodeVO> treeForSelect(int entid, long excludeId) {
		List<Frame> list = frameMapper.selectList(Wrappers.lambdaQuery(Frame.class)
			.eq(Frame::getEntid, (long) entid)
			.eq(Frame::getIsShow, 1)
			.isNull(Frame::getDeletedAt)
			.orderByDesc(Frame::getSort)
			.orderByAsc(Frame::getId));
		List<Long> disabledIds = new ArrayList<>();
		if (excludeId > 0) {
			disabledIds.add(excludeId);
			disabledIds.addAll(descendantIds(excludeId, entid));
		}
		List<FrameSelectTreeNodeVO> flat = new ArrayList<>();
		for (Frame f : list) {
			FrameSelectTreeNodeVO row = new FrameSelectTreeNodeVO();
			row.setValue(f.getId());
			row.setLabel(f.getName());
			row.setPid(f.getPid());
			if (excludeId > 0) {
				row.setDisabled(disabledIds.contains(f.getId()));
			}
			flat.add(row);
		}
		return TreeUtil.buildTree(flat, FrameSelectTreeNodeVO::getValue, FrameSelectTreeNodeVO::getPid,
				FrameSelectTreeNodeVO::getChildren);
	}

	private List<Long> descendantIds(long id, int entid) {
		return frameMapper
			.selectList(Wrappers.lambdaQuery(Frame.class)
				.eq(Frame::getEntid, (long) entid)
				.isNull(Frame::getDeletedAt)
				.like(Frame::getPath, "/" + id + "/"))
			.stream()
			.map(Frame::getId)
			.toList();
	}

	private void arrangePaths(int entid) {
		List<Frame> all = frameMapper.selectList(
				Wrappers.lambdaQuery(Frame.class).eq(Frame::getEntid, (long) entid).isNull(Frame::getDeletedAt));
		Map<Long, Frame> byId = all.stream().collect(Collectors.toMap(Frame::getId, x -> x, (a, b) -> a));
		for (Frame f : all) {
			String path = computePathString(f, byId);
			if (!Objects.equals(path, f.getPath())) {
				Frame u = new Frame();
				u.setId(f.getId());
				u.setPath(path);
				u.setUpdatedAt(LocalDateTime.now());
				frameMapper.updateById(u);
			}
		}
	}

	private static String computePathString(Frame f, Map<Long, Frame> byId) {
		LinkedList<Long> chain = new LinkedList<>();
		Frame cur = f;
		while (cur != null) {
			chain.addFirst(cur.getId());
			if (cur.getPid() == null || cur.getPid() == 0) {
				break;
			}
			cur = byId.get(cur.getPid().longValue());
		}
		StringBuilder sb = new StringBuilder();
		for (Long cid : chain) {
			sb.append("/").append(cid);
		}
		return sb.isEmpty() ? "/" : sb + "/";
	}

	private static FrameAuthTreeNodeVO toAuthNode(Frame f, boolean withRole) {
		FrameAuthTreeNodeVO n = new FrameAuthTreeNodeVO();
		n.setId(f.getId());
		n.setPid(f.getPid());
		n.setEntid(f.getEntid());
		n.setUserCount(f.getUserCount());
		n.setPath(f.getPath());
		n.setValue(f.getId());
		n.setLabel(f.getName());
		n.setName(f.getName());
		n.setUserSingleCount(f.getUserSingleCount());
		n.setDisabled(false);
		return n;
	}

	private static FrameAuthTreeNodeVO roleAuthNode(String id, String label, String value) {
		FrameAuthTreeNodeVO n = new FrameAuthTreeNodeVO();
		n.setId(id);
		n.setPid(0);
		n.setLabel(label);
		n.setName(label);
		n.setValue(value);
		n.setDisabled(false);
		return n;
	}

	private static FrameDetailVO toDetailVo(Frame f) {
		FrameDetailVO vo = new FrameDetailVO();
		vo.setId(f.getId());
		vo.setUserId(f.getUserId());
		vo.setEntid(f.getEntid());
		vo.setPid(f.getPid());
		vo.setRoleId(f.getRoleId());
		vo.setName(f.getName());
		vo.setPath(f.getPath());
		vo.setIntroduce(f.getIntroduce());
		vo.setSort(f.getSort());
		vo.setUserCount(f.getUserCount());
		vo.setUserSingleCount(f.getUserSingleCount());
		vo.setIsShow(f.getIsShow());
		vo.setLevel(f.getLevel());
		return vo;
	}

}
