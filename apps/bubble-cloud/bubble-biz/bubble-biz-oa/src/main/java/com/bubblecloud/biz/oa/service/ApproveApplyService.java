package com.bubblecloud.biz.oa.service;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.entity.ApproveApply;

/**
 * 审批申请主表服务。
 *
 * @author qinlei
 * @date 2026/4/6 12:00
 */
public interface ApproveApplyService extends UpService<ApproveApply> {

	void verifyApply(Long id, Long operatorId, int status);

	void revokeApply(Long id, Long operatorId, String info);

}
