package com.bubblecloud.biz.oa.service.impl;

import java.time.LocalDateTime;
import java.util.Objects;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.bubblecloud.biz.oa.mapper.ApproveReplyMapper;
import com.bubblecloud.biz.oa.service.ApproveReplyService;
import com.bubblecloud.biz.oa.util.OaSecurityUtil;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.ApproveReply;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 审批评价实现。
 *
 * @author qinlei
 * @date 2026/4/2 15:00
 */
@Service
public class ApproveReplyServiceImpl extends UpServiceImpl<ApproveReplyMapper, ApproveReply>
		implements ApproveReplyService {

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(ApproveReply dto) {
		Long uid = OaSecurityUtil.currentUserId();
		if (ObjectUtil.isNull(uid)) {
			throw new IllegalArgumentException("用户未登录");
		}
		if (ObjectUtil.isNull(dto.getApplyId())) {
			throw new IllegalArgumentException("申请ID不能为空");
		}
		LocalDateTime now = LocalDateTime.now();
		dto.setUserId(uid);
		dto.setCardId(uid);
		dto.setContent(StrUtil.nullToEmpty(dto.getContent()));
		dto.setCreatedAt(now);
		dto.setUpdatedAt(now);
		return super.create(dto);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R deleteById(Long id) {
		Long uid = OaSecurityUtil.currentUserId();
		if (ObjectUtil.isNull(uid)) {
			throw new IllegalArgumentException("用户未登录");
		}
		ApproveReply r = getById(id);
		if (ObjectUtil.isNull(r)) {
			throw new IllegalArgumentException("记录不存在");
		}
		if (!Objects.equals(r.getUserId(), uid)) {
			throw new IllegalArgumentException("仅可删除自己的评价！");
		}
		return super.deleteById(id);
	}

}
