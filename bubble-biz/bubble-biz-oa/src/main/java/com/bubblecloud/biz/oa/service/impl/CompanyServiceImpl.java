package com.bubblecloud.biz.oa.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bubblecloud.common.core.util.R;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.AdminMapper;
import com.bubblecloud.biz.oa.mapper.EnterpriseMapper;
import com.bubblecloud.biz.oa.mapper.FrameMapper;
import com.bubblecloud.biz.oa.mapper.UserEnterpriseApplyMapper;
import com.bubblecloud.biz.oa.service.CompanyService;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.dto.CompanyUpdateDTO;
import com.bubblecloud.oa.api.entity.Admin;
import com.bubblecloud.oa.api.entity.Enterprise;
import com.bubblecloud.oa.api.entity.Frame;
import com.bubblecloud.oa.api.vo.company.CompanyEntInfoVO;
import com.bubblecloud.oa.api.vo.company.CompanyOwnerUserVO;
import com.bubblecloud.oa.api.vo.company.CompanyQuantityVO;

import lombok.RequiredArgsConstructor;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 企业管理实现。
 *
 * @author qinlei
 * @date 2026/3/29 下午5:30
 */
@Service
@RequiredArgsConstructor
public class CompanyServiceImpl extends UpServiceImpl<EnterpriseMapper, Enterprise> implements CompanyService {

	private final AdminMapper adminMapper;

	private final FrameMapper frameMapper;

	private final UserEnterpriseApplyMapper userEnterpriseApplyMapper;

	@Override
	public CompanyEntInfoVO getEntAndUserInfo(Integer entId) {
		if (ObjectUtil.isNull(entId) || entId <= 0) {
			throw new IllegalArgumentException("企业ID不能为空");
		}
		Enterprise ent = this.getOne(Wrappers.lambdaQuery(Enterprise.class)
			.eq(Enterprise::getId, entId.longValue())
			.eq(Enterprise::getStatus, 1)
			.isNull(Enterprise::getDeleteTime));
		if (ObjectUtil.isNull(ent)) {
			throw new IllegalArgumentException("企业不存在");
		}
		CompanyOwnerUserVO owner = null;
		if (StrUtil.isNotBlank(ent.getUid())) {
			Admin admin = adminMapper.selectOne(
					Wrappers.lambdaQuery(Admin.class).eq(Admin::getUid, ent.getUid()).isNull(Admin::getDeletedAt));
			if (ObjectUtil.isNotNull(admin)) {
				owner = new CompanyOwnerUserVO(admin.getUid(), admin.getName());
			}
		}
		long frameCount = frameMapper.selectCount(
				Wrappers.lambdaQuery(Frame.class).eq(Frame::getEntid, entId.longValue()).isNull(Frame::getDeletedAt));
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
	public boolean updateEnt(Integer entId, CompanyUpdateDTO dto) {
		if (ObjectUtil.isNull(entId) || entId <= 0 || ObjectUtil.isNull(dto)) {
			return false;
		}
		Enterprise existing = this.getById(entId.longValue());
		if (ObjectUtil.isNull(existing)) {
			return false;
		}
		boolean nameChanged = StrUtil.isNotBlank(dto.getEnterpriseName());
		var uw = Wrappers.lambdaUpdate(Enterprise.class).eq(Enterprise::getId, entId.longValue());
		boolean hasField = false;
		if (ObjectUtil.isNotNull(dto.getLogo())) {
			uw.set(Enterprise::getLogo, dto.getLogo());
			hasField = true;
		}
		if (ObjectUtil.isNotNull(dto.getEnterpriseName())) {
			uw.set(Enterprise::getName, dto.getEnterpriseName());
			hasField = true;
		}
		if (ObjectUtil.isNotNull(dto.getProvince())) {
			uw.set(Enterprise::getProvince, dto.getProvince());
			hasField = true;
		}
		if (ObjectUtil.isNotNull(dto.getCity())) {
			uw.set(Enterprise::getCity, dto.getCity());
			hasField = true;
		}
		if (ObjectUtil.isNotNull(dto.getArea())) {
			uw.set(Enterprise::getArea, dto.getArea());
			hasField = true;
		}
		if (ObjectUtil.isNotNull(dto.getAddress())) {
			uw.set(Enterprise::getAddress, dto.getAddress());
			hasField = true;
		}
		if (ObjectUtil.isNotNull(dto.getPhone())) {
			uw.set(Enterprise::getPhone, dto.getPhone());
			hasField = true;
		}
		if (ObjectUtil.isNotNull(dto.getShortName())) {
			uw.set(Enterprise::getShortName, dto.getShortName());
			hasField = true;
		}
		if (!hasField) {
			return false;
		}
		int rows = this.baseMapper.update(null, uw);
		if (rows > 0 && nameChanged) {
			frameMapper.update(null,
					Wrappers.lambdaUpdate(Frame.class)
						.eq(Frame::getEntid, entId.longValue())
						.eq(Frame::getPid, 0)
						.isNull(Frame::getDeletedAt)
						.set(Frame::getName, dto.getEnterpriseName()));
		}
		return rows > 0;
	}

	@Override
	public CompanyQuantityVO getQuantity(String type, Integer entId) {
		if (StrUtil.isBlank(type)) {
			return new CompanyQuantityVO(0L);
		}
		return switch (type) {
			case "inviter_review" -> new CompanyQuantityVO(userEnterpriseApplyMapper.countInviterReview(entId));
			default -> new CompanyQuantityVO(0L);
		};
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(Enterprise req) {
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(Enterprise req) {
		return super.update(req);
	}

}
