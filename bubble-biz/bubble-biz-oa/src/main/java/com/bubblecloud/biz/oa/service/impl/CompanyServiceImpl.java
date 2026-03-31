package com.bubblecloud.biz.oa.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.AdminMapper;
import com.bubblecloud.biz.oa.mapper.EnterpriseMapper;
import com.bubblecloud.biz.oa.mapper.FrameMapper;
import com.bubblecloud.biz.oa.mapper.UserEnterpriseApplyMapper;
import com.bubblecloud.biz.oa.service.CompanyService;
import com.bubblecloud.oa.api.dto.CompanyUpdateDTO;
import com.bubblecloud.oa.api.entity.Admin;
import com.bubblecloud.oa.api.entity.Enterprise;
import com.bubblecloud.oa.api.entity.Frame;
import com.bubblecloud.oa.api.vo.company.CompanyEntInfoVO;
import com.bubblecloud.oa.api.vo.company.CompanyOwnerUserVO;
import com.bubblecloud.oa.api.vo.company.CompanyQuantityVO;

import lombok.RequiredArgsConstructor;

/**
 * 企业管理实现。
 *
 * @author qinlei
 * @date 2026/3/29 下午5:30
 */
@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

	private final EnterpriseMapper enterpriseMapper;

	private final AdminMapper adminMapper;

	private final FrameMapper frameMapper;

	private final UserEnterpriseApplyMapper userEnterpriseApplyMapper;

	@Override
	public CompanyEntInfoVO getEntAndUserInfo(int entId) {
		if (entId <= 0) {
			throw new IllegalArgumentException("企业ID不能为空");
		}
		Enterprise ent = enterpriseMapper.selectOne(Wrappers.lambdaQuery(Enterprise.class)
			.eq(Enterprise::getId, (long) entId)
			.eq(Enterprise::getStatus, 1)
			.isNull(Enterprise::getDeleteTime));
		if (ent == null) {
			throw new IllegalArgumentException("企业不存在");
		}
		CompanyOwnerUserVO owner = null;
		if (StringUtils.hasText(ent.getUid())) {
			Admin admin = adminMapper.selectOne(
					Wrappers.lambdaQuery(Admin.class).eq(Admin::getUid, ent.getUid()).isNull(Admin::getDeletedAt));
			if (admin != null) {
				owner = new CompanyOwnerUserVO(admin.getUid(), admin.getName());
			}
		}
		long frameCount = frameMapper.selectCount(
				Wrappers.lambdaQuery(Frame.class).eq(Frame::getEntid, (long) entId).isNull(Frame::getDeletedAt));
		long enterpriseCount = adminMapper
			.selectCount(Wrappers.lambdaQuery(Admin.class).eq(Admin::getStatus, 1).isNull(Admin::getDeletedAt));
		CompanyEntInfoVO vo = new CompanyEntInfoVO();
		vo.setId(ent.getId());
		vo.setLogo(ent.getLogo());
		vo.setTitle(ent.getTitle());
		vo.setEnterpriseName(ent.getName());
		vo.setEnterpriseNameEn(ent.getEnterpriseNameEn());
		vo.setShortName(ent.getShortName());
		vo.setProvince(ent.getProvince());
		vo.setCity(ent.getCity());
		vo.setArea(ent.getArea());
		vo.setAddress(ent.getAddress());
		vo.setPhone(ent.getPhone());
		vo.setUid(ent.getUid());
		vo.setStatus(ent.getStatus());
		vo.setUser(owner);
		vo.setFrames((int) frameCount);
		vo.setEnterprises(enterpriseCount);
		return vo;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean updateEnt(int entId, CompanyUpdateDTO dto) {
		if (entId <= 0 || dto == null) {
			return false;
		}
		Enterprise existing = enterpriseMapper.selectById((long) entId);
		if (existing == null) {
			return false;
		}
		boolean nameChanged = StringUtils.hasText(dto.getEnterpriseName());
		var uw = Wrappers.lambdaUpdate(Enterprise.class).eq(Enterprise::getId, (long) entId);
		boolean hasField = false;
		if (dto.getLogo() != null) {
			uw.set(Enterprise::getLogo, dto.getLogo());
			hasField = true;
		}
		if (dto.getEnterpriseName() != null) {
			uw.set(Enterprise::getName, dto.getEnterpriseName());
			hasField = true;
		}
		if (dto.getProvince() != null) {
			uw.set(Enterprise::getProvince, dto.getProvince());
			hasField = true;
		}
		if (dto.getCity() != null) {
			uw.set(Enterprise::getCity, dto.getCity());
			hasField = true;
		}
		if (dto.getArea() != null) {
			uw.set(Enterprise::getArea, dto.getArea());
			hasField = true;
		}
		if (dto.getAddress() != null) {
			uw.set(Enterprise::getAddress, dto.getAddress());
			hasField = true;
		}
		if (dto.getPhone() != null) {
			uw.set(Enterprise::getPhone, dto.getPhone());
			hasField = true;
		}
		if (dto.getShortName() != null) {
			uw.set(Enterprise::getShortName, dto.getShortName());
			hasField = true;
		}
		if (!hasField) {
			return false;
		}
		int rows = enterpriseMapper.update(null, uw);
		if (rows > 0 && nameChanged) {
			frameMapper.update(null,
					Wrappers.lambdaUpdate(Frame.class)
						.eq(Frame::getEntid, (long) entId)
						.eq(Frame::getPid, 0)
						.isNull(Frame::getDeletedAt)
						.set(Frame::getName, dto.getEnterpriseName()));
		}
		return rows > 0;
	}

	@Override
	public CompanyQuantityVO getQuantity(String type, int entId) {
		if (!StringUtils.hasText(type)) {
			return new CompanyQuantityVO(0L);
		}
		return switch (type) {
			case "inviter_review" -> new CompanyQuantityVO(userEnterpriseApplyMapper.countInviterReview(entId));
			default -> new CompanyQuantityVO(0L);
		};
	}

}
