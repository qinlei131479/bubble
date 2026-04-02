package com.bubblecloud.biz.oa.service.impl;

import com.bubblecloud.common.core.util.PojoConvertUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bubblecloud.common.core.util.R;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.AdminMapper;
import com.bubblecloud.biz.oa.mapper.EnterpriseMapper;
import com.bubblecloud.biz.oa.mapper.FrameMapper;
import com.bubblecloud.biz.oa.mapper.UserEnterpriseApplyMapper;
import com.bubblecloud.biz.oa.service.EnterpriseService;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.Admin;
import com.bubblecloud.oa.api.entity.Enterprise;
import com.bubblecloud.oa.api.entity.Frame;
import com.bubblecloud.oa.api.vo.enterprise.EnterpriseEntInfoVO;
import com.bubblecloud.oa.api.vo.enterprise.EnterpriseOwnerUserVO;
import com.bubblecloud.oa.api.vo.enterprise.EnterpriseQuantityVO;

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
public class EnterpriseServiceImpl extends UpServiceImpl<EnterpriseMapper, Enterprise> implements EnterpriseService {

	private final AdminMapper adminMapper;

	private final FrameMapper frameMapper;

	private final UserEnterpriseApplyMapper userEnterpriseApplyMapper;

	@Override
	public EnterpriseEntInfoVO getEntAndUserInfo(Long entId) {
		Enterprise ent = this.getOne(Wrappers.lambdaQuery(Enterprise.class)
				.eq(Enterprise::getId, entId)
				.eq(Enterprise::getStatus, 1)
				.isNull(Enterprise::getDeleteTime));
		if (ObjectUtil.isNull(ent)) {
			throw new IllegalArgumentException("企业不存在");
		}
		EnterpriseOwnerUserVO owner = null;
		if (StrUtil.isNotBlank(ent.getUid())) {
			Admin admin = adminMapper.selectOne(
					Wrappers.lambdaQuery(Admin.class).eq(Admin::getUid, ent.getUid()).isNull(Admin::getDeletedAt));
			if (ObjectUtil.isNotNull(admin)) {
				owner = new EnterpriseOwnerUserVO(admin.getUid(), admin.getName());
			}
		}
		long frameCount = frameMapper.selectCount(
				Wrappers.lambdaQuery(Frame.class).eq(Frame::getEntid, entId.longValue()).isNull(Frame::getDeletedAt));
		long enterpriseCount = adminMapper
				.selectCount(Wrappers.lambdaQuery(Admin.class).eq(Admin::getStatus, 1).isNull(Admin::getDeletedAt));
		EnterpriseEntInfoVO vo = PojoConvertUtil.convertPojo(ent, EnterpriseEntInfoVO.class);
		vo.setEnterpriseName(ent.getName());
		vo.setEnterpriseNameEn(ent.getEnterpriseNameEn());
		vo.setUser(owner);
		vo.setFrames((int) frameCount);
		vo.setEnterprises(enterpriseCount);
		return vo;
	}

	@Override
	public EnterpriseQuantityVO getQuantity(String type, Long entId) {
		if (StrUtil.isBlank(type)) {
			return new EnterpriseQuantityVO(0L);
		}
		return switch (type) {
			case "inviter_review" -> new EnterpriseQuantityVO(userEnterpriseApplyMapper.countInviterReview(entId));
			default -> new EnterpriseQuantityVO(0L);
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
		int update = this.baseMapper.updateById(req);
		if (update > 0) {
			frameMapper.update(null,
					Wrappers.lambdaUpdate(Frame.class)
							.eq(Frame::getEntid, req.getId())
							.eq(Frame::getPid, 0)
							.isNull(Frame::getDeletedAt)
							.set(Frame::getName, req.getName()));
		}
		return R.ok();
	}

}
