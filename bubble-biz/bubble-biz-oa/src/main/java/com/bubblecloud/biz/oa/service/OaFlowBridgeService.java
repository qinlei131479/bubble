package com.bubblecloud.biz.oa.service;

import org.springframework.stereotype.Service;

import com.bubblecloud.flow.api.feign.RemoteFlowService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * OA → bubble-biz-flow 桥接（W-28）；调用失败仅记录日志，不阻断主流程。
 *
 * @author qinlei
 * @date 2026/4/6 16:30
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OaFlowBridgeService {

	private final RemoteFlowService remoteFlowService;

	/**
	 * 新建审批申请后的占位通知（后续可换真实流程实例启动）。
	 */
	public void afterApplyCreated(Long applyId) {
		if (applyId == null) {
			return;
		}
		try {
			remoteFlowService.notifyOaApply(applyId);
		}
		catch (Exception e) {
			log.warn("flow notifyOaApply skipped applyId={} msg={}", applyId, e.getMessage());
		}
	}

}
