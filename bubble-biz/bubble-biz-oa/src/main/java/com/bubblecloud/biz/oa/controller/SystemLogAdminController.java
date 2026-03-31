package com.bubblecloud.biz.oa.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.service.SystemLogAdminService;
import com.bubblecloud.biz.oa.support.PhpResponse;
import com.bubblecloud.oa.api.entity.EnterpriseLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统管理-操作日志（对齐 PHP {@code ent/system/log}，默认 eb_enterprise_log_1）。
 *
 * @author qinlei
 * @date 2026/3/30
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/system/log")
@Tag(name = "系统日志")
public class SystemLogAdminController {

	private final SystemLogAdminService systemLogAdminService;

	@GetMapping
	@Operation(summary = "日志分页")
	public PhpResponse<Page<EnterpriseLog>> index(@RequestParam(required = false) String user_name,
			@RequestParam(required = false) String path, @RequestParam(required = false) String event_name,
			@RequestParam(defaultValue = "1") int entid, @RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "20") int limit) {
		return PhpResponse
			.ok(systemLogAdminService.pageList(user_name, path, event_name, entid, page, limit));
	}

}
