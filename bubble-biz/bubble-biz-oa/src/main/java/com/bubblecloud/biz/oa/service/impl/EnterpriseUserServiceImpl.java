package com.bubblecloud.biz.oa.service.impl;

import java.util.List;
import com.bubblecloud.common.core.util.R;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.mapper.AdminMapper;
import com.bubblecloud.biz.oa.mapper.AssessScoreMapper;
import com.bubblecloud.biz.oa.mapper.FrameAssistMapper;
import com.bubblecloud.biz.oa.mapper.SystemConfigMapper;
import com.bubblecloud.biz.oa.service.EnterpriseUserService;
import com.bubblecloud.biz.oa.service.FrameAssistWriteService;
import com.bubblecloud.biz.oa.service.FrameService;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.dto.EnterpriseUserCardUpdateDTO;
import com.bubblecloud.oa.api.dto.FrameAssistView;
import com.bubblecloud.oa.api.entity.Admin;
import com.bubblecloud.oa.api.entity.SystemConfig;
import com.bubblecloud.oa.api.vo.SimplePageVO;
import com.bubblecloud.oa.api.vo.enterprise.EnterpriseUserCardVO;
import com.bubblecloud.oa.api.vo.enterprise.EnterpriseUserListItemVO;
import com.bubblecloud.oa.api.vo.enterprise.EnterpriseUserProfileVO;
import com.bubblecloud.oa.api.vo.enterprise.UserFrameBriefVO;
import com.bubblecloud.oa.api.vo.frame.FrameDepartmentTreeNodeVO;

import lombok.RequiredArgsConstructor;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 企业用户业务实现。
 *
 * @author qinlei
 * @date 2026/3/29 下午5:30
 */
@Service
@RequiredArgsConstructor
public class EnterpriseUserServiceImpl extends UpServiceImpl<AdminMapper, Admin> implements EnterpriseUserService {

	private final FrameAssistMapper frameAssistMapper;

	private final AssessScoreMapper assessScoreMapper;

	private final SystemConfigMapper systemConfigMapper;

	private final FrameService frameService;

	private final FrameAssistWriteService frameAssistWriteService;

	@Override
	public SimplePageVO listEnterpriseUsers(Integer entid, String pid, String name, Integer status, Integer current, Integer size) {
		Page<EnterpriseUserListItemVO> p = new Page<>(current, size);
		Page<EnterpriseUserListItemVO> r = baseMapper.selectEntUserList(p, entid, name,
				ObjectUtil.isNull(status) ? 1 : status);
		return SimplePageVO.of((int) r.getCurrent(), (int) r.getSize(), r.getTotal(), r.getRecords());
	}

	@Override
	public SimplePageVO addressBook(Integer entid, String name, Integer status, Integer current, Integer size) {
		Page<EnterpriseUserListItemVO> p = new Page<>(current, size);
		Page<EnterpriseUserListItemVO> r = baseMapper.selectEntUserList(p, entid, name, status);
		return SimplePageVO.of((int) r.getCurrent(), (int) r.getSize(), r.getTotal(), r.getRecords());
	}

	@Override
	public EnterpriseUserProfileVO userInfo(Long adminId, Integer entid) {
		Admin admin = baseMapper.selectById(adminId);
		if (ObjectUtil.isNull(admin)) {
			throw new IllegalArgumentException("未找到用户信息");
		}
		Integer max = assessScoreMapper.selectMaxScoreByEntid(entid.longValue());
		if (ObjectUtil.isNull(max)) {
			max = 0;
		}
		int computeMode = 1;
		SystemConfig cfg = systemConfigMapper
			.selectOne(Wrappers.lambdaQuery(SystemConfig.class).eq(SystemConfig::getConfigKey, "assess_compute_mode"));
		if (ObjectUtil.isNotNull(cfg) && ObjectUtil.isNotNull(cfg.getValue()) && !cfg.getValue().isEmpty()
				&& !"1".equals(cfg.getValue()) && !"true".equalsIgnoreCase(cfg.getValue())) {
			computeMode = 0;
		}
		EnterpriseUserProfileVO vo = new EnterpriseUserProfileVO();
		vo.setId(admin.getId());
		vo.setUid(admin.getUid());
		vo.setName(admin.getName());
		vo.setAvatar(admin.getAvatar());
		vo.setPhone(admin.getPhone());
		vo.setJob(admin.getJob());
		vo.setStatus(admin.getStatus());
		vo.setAccount(admin.getAccount());
		vo.setEntIds(List.of(entid));
		vo.setJobId(admin.getJob());
		vo.setMaxScore(max);
		vo.setComputeMode(computeMode);
		return vo;
	}

	@Override
	public UserFrameBriefVO userFrame(Long adminId, Integer entid) {
		List<FrameAssistView> frames = frameAssistMapper.selectUserFrames(adminId, entid);
		if (frames.isEmpty()) {
			throw new IllegalArgumentException("未找到用户部门信息");
		}
		FrameAssistView v = frames.get(0);
		UserFrameBriefVO vo = new UserFrameBriefVO();
		vo.setId(ObjectUtil.isNull(v.getFrameId()) ? null : v.getFrameId().longValue());
		vo.setName(v.getFrameName());
		vo.setEntid(entid.longValue());
		return vo;
	}

	@Override
	public List<FrameDepartmentTreeNodeVO> addressBookTree(Integer entid, String name) {
		List<FrameDepartmentTreeNodeVO> tree = frameService.departmentTreeList(1, entid);
		if (StrUtil.isBlank(name)) {
			return tree;
		}
		return filterTreeByName(tree, name.trim());
	}

	private static List<FrameDepartmentTreeNodeVO> filterTreeByName(List<FrameDepartmentTreeNodeVO> nodes,
			String keyword) {
		if (ObjectUtil.isNull(nodes) || nodes.isEmpty()) {
			return List.of();
		}
		return nodes.stream().map(n -> filterNode(n, keyword)).filter(java.util.Objects::nonNull).toList();
	}

	@Override
	public EnterpriseUserCardVO getCardEdit(Long targetAdminId, Integer entid) {
		if (frameAssistMapper.selectUserFrames(targetAdminId, entid).isEmpty()) {
			throw new IllegalArgumentException("企业用户信息不存在");
		}
		Admin admin = baseMapper.selectById(targetAdminId);
		if (ObjectUtil.isNull(admin)) {
			throw new IllegalArgumentException("企业用户信息不存在");
		}
		EnterpriseUserCardVO vo = new EnterpriseUserCardVO();
		vo.setId(admin.getId());
		vo.setUid(admin.getUid());
		vo.setName(admin.getName());
		vo.setPhone(admin.getPhone());
		vo.setAvatar(admin.getAvatar());
		vo.setJob(admin.getJob());
		return vo;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateEnterpriseUserCard(Long targetAdminId, Integer entid, EnterpriseUserCardUpdateDTO dto) {
		if (ObjectUtil.isNull(dto.getFrameId()) || dto.getFrameId().isEmpty()) {
			throw new IllegalArgumentException("必须选择一个部门");
		}
		if (ObjectUtil.isNull(dto.getMastartId()) || dto.getMastartId() == 0) {
			throw new IllegalArgumentException("必须选择一个主部门");
		}
		if (!dto.getFrameId().contains(dto.getMastartId())) {
			throw new IllegalArgumentException("主部门必须在所选部门中");
		}
		boolean isAdmin = ObjectUtil.isNotNull(dto.getIsAdmin()) && dto.getIsAdmin() == 1;
		if (isAdmin && (ObjectUtil.isNull(dto.getManageFrames()) || dto.getManageFrames().isEmpty())) {
			throw new IllegalArgumentException("必须选择一个负责部门");
		}
		Admin target = baseMapper.selectById(targetAdminId);
		if (ObjectUtil.isNull(target)) {
			throw new IllegalArgumentException("修改的用户不存在");
		}
		if (StrUtil.isNotBlank(dto.getPhone())) {
			long cnt = baseMapper.selectCount(Wrappers.lambdaQuery(Admin.class)
				.eq(Admin::getPhone, dto.getPhone())
				.ne(Admin::getId, targetAdminId)
				.isNull(Admin::getDeletedAt));
			if (cnt > 0) {
				throw new IllegalArgumentException("该手机号已存在");
			}
		}
		if (StrUtil.isNotBlank(dto.getName())) {
			target.setName(dto.getName());
		}
		if (StrUtil.isNotBlank(dto.getPhone())) {
			target.setPhone(dto.getPhone());
		}
		if (StrUtil.isNotBlank(dto.getPosition())) {
			try {
				target.setJob(Integer.parseInt(dto.getPosition().trim()));
			}
			catch (NumberFormatException e) {
				// 保持原岗位
			}
		}
		baseMapper.updateById(target);

		long sup = ObjectUtil.isNull(dto.getSuperiorUid()) ? 0L : dto.getSuperiorUid();
		frameAssistWriteService.setUserFrames(entid, targetAdminId, dto.getFrameId(), dto.getMastartId(), isAdmin, sup,
				ObjectUtil.isNull(dto.getManageFrames()) ? List.of() : dto.getManageFrames());
	}

	private static FrameDepartmentTreeNodeVO filterNode(FrameDepartmentTreeNodeVO n, String keyword) {
		List<FrameDepartmentTreeNodeVO> children = n.getChildren();
		List<FrameDepartmentTreeNodeVO> filteredChildren = ObjectUtil.isNull(children) ? List.of()
				: filterTreeByName(children, keyword);
		boolean selfMatch = ObjectUtil.isNotNull(n.getLabel()) && n.getLabel().contains(keyword);
		if (selfMatch || !filteredChildren.isEmpty()) {
			FrameDepartmentTreeNodeVO copy = new FrameDepartmentTreeNodeVO();
			copy.setPid(n.getPid());
			copy.setPath(n.getPath());
			copy.setValue(n.getValue());
			copy.setLabel(n.getLabel());
			copy.setUserCount(n.getUserCount());
			copy.setUserSingleCount(n.getUserSingleCount());
			copy.setIsCheck(n.getIsCheck());
			copy.setChildren(filteredChildren);
			return copy;
		}
		return null;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(Admin req) {
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(Admin req) {
		return super.update(req);
	}

}
