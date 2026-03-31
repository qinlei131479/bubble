package com.bubblecloud.biz.oa.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.service.SystemAttachAdminService;
import com.bubblecloud.biz.oa.support.PhpResponse;
import com.bubblecloud.oa.api.entity.SystemAttach;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统附件（对齐 PHP {@code ent/system/attach}）。
 *
 * @author qinlei
 * @date 2026/3/30
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/system/attach")
@Tag(name = "系统附件")
public class SystemAttachAdminController {

	private final SystemAttachAdminService systemAttachAdminService;

	@GetMapping
	@Operation(summary = "附件分页")
	public PhpResponse<Page<SystemAttach>> index(@RequestParam(defaultValue = "1") int entid,
			@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int limit) {
		return PhpResponse.ok(systemAttachAdminService.pageList(entid, page, limit));
	}

}
