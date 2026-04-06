package com.bubblecloud.flow.api.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bubblecloud.common.core.util.R;

/**
 * OA 调用流程服务的内部桥接（W-28：与审批/低代码审批链路对接前的占位契约）。
 *
 * @author qinlei
 * @date 2026/4/6 16:00
 */
@FeignClient(contextId = "remoteFlowService", value = "bubble-biz-flow", path = "/internal/workflow")
public interface RemoteFlowService {

	/**
	 * 健康探测（注册中心可达性）。
	 */
	@GetMapping("/ping")
	R<String> ping();

	/**
	 * 审批实例占位：记录业务主键，后续接 Flowable/Camunda。
	 */
	@GetMapping("/oa-apply-notify")
	R<String> notifyOaApply(@RequestParam("applyId") Long applyId);

}
