package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.biz.oa.service.QuickAdminService;
import com.bubblecloud.biz.oa.support.PhpResponse;
import com.bubblecloud.oa.api.entity.SystemQuick;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 快捷入口（对齐 PHP {@code ent/config/quick}）。
 *
 * @author qinlei
 * @date 2026/3/30
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/config/quick")
@Tag(name = "快捷入口")
public class QuickAdminController {

	private final QuickAdminService quickAdminService;

	@GetMapping
	@Operation(summary = "分页列表")
	public PhpResponse<?> index(@RequestParam(required = false) Integer cid,
			@RequestParam(required = false) String name, @RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "20") int limit) {
		return PhpResponse.ok(quickAdminService.page(cid, name, page, limit));
	}

	@GetMapping("/create")
	@Operation(summary = "创建占位")
	public PhpResponse<String> create(@RequestParam(required = false) String cid) {
		return PhpResponse.ok("ok");
	}

	@PostMapping
	@Operation(summary = "新增")
	public PhpResponse<String> store(@RequestBody SystemQuick body) {
		quickAdminService.saveQuick(body);
		return PhpResponse.ok("common.insert.succ");
	}

	@GetMapping("/{id}/edit")
	@Operation(summary = "编辑数据")
	public PhpResponse<SystemQuick> edit(@PathVariable int id) {
		return PhpResponse.ok(quickAdminService.getQuick(id));
	}

	@PutMapping("/{id}")
	@Operation(summary = "修改保存")
	public PhpResponse<String> update(@PathVariable int id, @RequestBody SystemQuick body) {
		body.setId(id);
		quickAdminService.updateQuick(body);
		return PhpResponse.ok("common.update.succ");
	}

	@GetMapping("/{id}")
	@Operation(summary = "显示/隐藏占位（PHP show）")
	public PhpResponse<String> show(@PathVariable int id) {
		return PhpResponse.ok("ok");
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除")
	public PhpResponse<String> destroy(@PathVariable int id) {
		quickAdminService.deleteQuick(id);
		return PhpResponse.ok("common.delete.succ");
	}

}
