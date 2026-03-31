package com.bubblecloud.biz.oa.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.mapper.AdminMapper;
import com.bubblecloud.biz.oa.mapper.AssessScoreMapper;
import com.bubblecloud.biz.oa.mapper.FrameAssistMapper;
import com.bubblecloud.biz.oa.mapper.SystemConfigMapper;
import com.bubblecloud.biz.oa.service.CompanyUserService;
import com.bubblecloud.biz.oa.service.FrameAssistWriteService;
import com.bubblecloud.biz.oa.service.FrameService;
import com.bubblecloud.oa.api.dto.EnterpriseUserCardUpdateDTO;
import com.bubblecloud.oa.api.dto.FrameAssistView;
import com.bubblecloud.oa.api.entity.Admin;
import com.bubblecloud.oa.api.entity.SystemConfig;
import com.bubblecloud.oa.api.vo.SimplePageVO;
import com.bubblecloud.oa.api.vo.company.CompanyUserCardVO;
import com.bubblecloud.oa.api.vo.company.CompanyUserListItemVO;
import com.bubblecloud.oa.api.vo.company.CompanyUserProfileVO;
import com.bubblecloud.oa.api.vo.company.UserFrameBriefVO;
import com.bubblecloud.oa.api.vo.frame.FrameDepartmentTreeNodeVO;

import lombok.RequiredArgsConstructor;

/**
 * 企业用户业务实现。
 *
 * @author qinlei
 * @date 2026/3/29 下午5:30
 */
@Service
@RequiredArgsConstructor
public class CompanyUserServiceImpl implements CompanyUserService {

	private final AdminMapper adminMapper;

	private final FrameAssistMapper frameAssistMapper;

	private final AssessScoreMapper assessScoreMapper;

	private final SystemConfigMapper systemConfigMapper;

	private final FrameService frameService;

	private final FrameAssistWriteService frameAssistWriteService;

	@Override
	public SimplePageVO listCompanyUsers(int entid, String pid, String name, Integer status, int current, int size) {
		Page<CompanyUserListItemVO> p = new Page<>(current, size);
		Page<CompanyUserListItemVO> r = adminMapper.selectEntUserList(p, entid, name, status == null ? 1 : status);
		return SimplePageVO.of((int) r.getCurrent(), (int) r.getSize(), r.getTotal(), r.getRecords());
	}

	@Override
	public SimplePageVO addressBook(int entid, String name, Integer status, int current, int size) {
		Page<CompanyUserListItemVO> p = new Page<>(current, size);
		Page<CompanyUserListItemVO> r = adminMapper.selectEntUserList(p, entid, name, status);
		return SimplePageVO.of((int) r.getCurrent(), (int) r.getSize(), r.getTotal(), r.getRecords());
	}

	@Override
	public CompanyUserProfileVO userInfo(long adminId, int entid) {
		Admin admin = adminMapper.selectById(adminId);
		if (admin == null) {
			throw new IllegalArgumentException("未找到用户信息");
		}
		Integer max = assessScoreMapper.selectMaxScoreByEntid((long) entid);
		if (max == null) {
			max = 0;
		}
		int computeMode = 1;
		SystemConfig cfg = systemConfigMapper
			.selectOne(Wrappers.lambdaQuery(SystemConfig.class).eq(SystemConfig::getConfigKey, "assess_compute_mode"));
		if (cfg != null && cfg.getValue() != null && !cfg.getValue().isEmpty() && !"1".equals(cfg.getValue())
				&& !"true".equalsIgnoreCase(cfg.getValue())) {
			computeMode = 0;
		}
		CompanyUserProfileVO vo = new CompanyUserProfileVO();
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
	public UserFrameBriefVO userFrame(long adminId, int entid) {
		List<FrameAssistView> frames = frameAssistMapper.selectUserFrames(adminId, entid);
		if (frames.isEmpty()) {
			throw new IllegalArgumentException("未找到用户部门信息");
		}
		FrameAssistView v = frames.get(0);
		UserFrameBriefVO vo = new UserFrameBriefVO();
		vo.setId(v.getFrameId() == null ? null : v.getFrameId().longValue());
		vo.setName(v.getFrameName());
		vo.setEntid((long) entid);
		return vo;
	}

	@Override
	public List<FrameDepartmentTreeNodeVO> addressBookTree(int entid, String name) {
		List<FrameDepartmentTreeNodeVO> tree = frameService.departmentTreeList(1, entid);
		if (name == null || name.isBlank()) {
			return tree;
		}
		return filterTreeByName(tree, name.trim());
	}

	private static List<FrameDepartmentTreeNodeVO> filterTreeByName(List<FrameDepartmentTreeNodeVO> nodes,
			String keyword) {
		if (nodes == null || nodes.isEmpty()) {
			return List.of();
		}
		return nodes.stream().map(n -> filterNode(n, keyword)).filter(java.util.Objects::nonNull).toList();
	}

	@Override
	public CompanyUserCardVO getCardEdit(long targetAdminId, int entid) {
		if (frameAssistMapper.selectUserFrames(targetAdminId, entid).isEmpty()) {
			throw new IllegalArgumentException("企业用户信息不存在");
		}
		Admin admin = adminMapper.selectById(targetAdminId);
		if (admin == null) {
			throw new IllegalArgumentException("企业用户信息不存在");
		}
		CompanyUserCardVO vo = new CompanyUserCardVO();
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
	public void updateCompanyUserCard(long targetAdminId, int entid, EnterpriseUserCardUpdateDTO dto) {
		if (dto.getFrameId() == null || dto.getFrameId().isEmpty()) {
			throw new IllegalArgumentException("必须选择一个部门");
		}
		if (dto.getMastartId() == null || dto.getMastartId() == 0) {
			throw new IllegalArgumentException("必须选择一个主部门");
		}
		if (!dto.getFrameId().contains(dto.getMastartId())) {
			throw new IllegalArgumentException("主部门必须在所选部门中");
		}
		boolean isAdmin = dto.getIsAdmin() != null && dto.getIsAdmin() == 1;
		if (isAdmin && (dto.getManageFrames() == null || dto.getManageFrames().isEmpty())) {
			throw new IllegalArgumentException("必须选择一个负责部门");
		}
		Admin target = adminMapper.selectById(targetAdminId);
		if (target == null) {
			throw new IllegalArgumentException("修改的用户不存在");
		}
		if (StringUtils.hasText(dto.getPhone())) {
			long cnt = adminMapper.selectCount(Wrappers.lambdaQuery(Admin.class)
				.eq(Admin::getPhone, dto.getPhone())
				.ne(Admin::getId, targetAdminId)
				.isNull(Admin::getDeletedAt));
			if (cnt > 0) {
				throw new IllegalArgumentException("该手机号已存在");
			}
		}
		if (StringUtils.hasText(dto.getName())) {
			target.setName(dto.getName());
		}
		if (StringUtils.hasText(dto.getPhone())) {
			target.setPhone(dto.getPhone());
		}
		if (StringUtils.hasText(dto.getPosition())) {
			try {
				target.setJob(Integer.parseInt(dto.getPosition().trim()));
			}
			catch (NumberFormatException e) {
				// 保持原岗位
			}
		}
		adminMapper.updateById(target);

		long sup = dto.getSuperiorUid() == null ? 0L : dto.getSuperiorUid();
		frameAssistWriteService.setUserFrames(entid, targetAdminId, dto.getFrameId(), dto.getMastartId(), isAdmin, sup,
				dto.getManageFrames() == null ? List.of() : dto.getManageFrames());
	}

	private static FrameDepartmentTreeNodeVO filterNode(FrameDepartmentTreeNodeVO n, String keyword) {
		List<FrameDepartmentTreeNodeVO> children = n.getChildren();
		List<FrameDepartmentTreeNodeVO> filteredChildren = children == null ? List.of()
				: filterTreeByName(children, keyword);
		boolean selfMatch = n.getLabel() != null && n.getLabel().contains(keyword);
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

}
