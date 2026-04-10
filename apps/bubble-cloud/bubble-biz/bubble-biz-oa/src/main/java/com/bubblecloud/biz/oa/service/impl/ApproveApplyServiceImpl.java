package com.bubblecloud.biz.oa.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.mapper.ApproveApplyMapper;
import com.bubblecloud.biz.oa.service.ApproveApplyService;
import com.bubblecloud.biz.oa.service.AttendanceApplyRecordSyncService;
import com.bubblecloud.biz.oa.service.OaFlowBridgeService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.ApproveApply;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 审批申请实现（流程引擎对接前：状态位占位更新）。
 *
 * @author qinlei
 * @date 2026/4/6 12:00
 */
@Service
public class ApproveApplyServiceImpl extends UpServiceImpl<ApproveApplyMapper, ApproveApply>
		implements ApproveApplyService {

	@Autowired
	private OaFlowBridgeService oaFlowBridgeService;

	@Autowired
	private AttendanceApplyRecordSyncService attendanceApplyRecordSyncService;

	@Override
	public Page<ApproveApply> findPg(Page page, ApproveApply query) {
		if (ObjectUtil.isNull(query)) {
			query = new ApproveApply();
		}
		var w = Wrappers.lambdaQuery(ApproveApply.class)
			.eq(ObjectUtil.isNotNull(query.getFilterEntid()), ApproveApply::getEntid, query.getFilterEntid())
			.orderByDesc(ApproveApply::getId);
		return baseMapper.selectPage(page, w);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void verifyApply(Long id, Long operatorId, int status) {
		ApproveApply a = baseMapper.selectById(id);
		if (ObjectUtil.isNull(a)) {
			throw new IllegalArgumentException("记录不存在");
		}
		a.setStatus(status);
		baseMapper.updateById(a);
		if (status == 1) {
			attendanceApplyRecordSyncService.createFromPassedApply(id);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void revokeApply(Long id, Long operatorId, String info) {
		ApproveApply a = baseMapper.selectById(id);
		if (ObjectUtil.isNull(a)) {
			throw new IllegalArgumentException("记录不存在");
		}
		a.setStatus(-1);
		if (StrUtil.isNotBlank(info)) {
			a.setInfo(StrUtil.nullToEmpty(info));
		}
		baseMapper.updateById(a);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(ApproveApply req) {
		R r = super.create(req);
		if (ObjectUtil.isNotNull(req.getId())) {
			oaFlowBridgeService.afterApplyCreated(req.getId());
		}
		return r;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(ApproveApply req) {
		return super.update(req);
	}

}
