package com.bubblecloud.biz.oa.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.service.SystemMessageAdminService;
import com.bubblecloud.biz.oa.support.PhpResponse;
import com.bubblecloud.oa.api.entity.Message;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统消息配置（对齐 PHP {@code ent/system/message}）。
 *
 * @author qinlei
 * @date 2026/3/30
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/system/message")
@Tag(name = "系统消息配置")
public class SystemMessageAdminController {

	private final SystemMessageAdminService systemMessageAdminService;

	@GetMapping("/list")
	@Operation(summary = "消息列表")
	public PhpResponse<Page<Message>> index(@RequestParam(required = false) Integer cate_id,
			@RequestParam(required = false) String title, @RequestParam(defaultValue = "1") int entid,
			@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int limit) {
		return PhpResponse.ok(systemMessageAdminService.pageList(cate_id, title, entid, page, limit));
	}

	@GetMapping("/find/{id}")
	@Operation(summary = "消息详情")
	public PhpResponse<Message> find(@PathVariable long id) {
		return PhpResponse.ok(systemMessageAdminService.getById(id));
	}

}
