package com.bubblecloud.biz.flow.controller;

import com.bubblecloud.common.core.util.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * OA 与流程服务的内部契约（W-28，Flowable/Camunda 前占位）。
 *
 * @author qinlei
 * @date 2026/4/6 16:30
 */
@RestController
@RequestMapping("/internal/workflow")
@Tag(name = "流程内部接口")
public class WorkflowInternalController {

	@GetMapping("/ping")
	@Operation(summary = "流程服务探测")
	public R<String> ping() {
		return R.ok("pong");
	}

	@GetMapping("/oa-apply-notify")
	@Operation(summary = "审批申请创建回调占位")
	public R<String> notifyOaApply(@RequestParam("applyId") Long applyId) {
		return R.ok("ack:" + applyId);
	}

}
