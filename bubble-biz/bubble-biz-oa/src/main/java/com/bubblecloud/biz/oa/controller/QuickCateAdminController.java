package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.biz.oa.service.QuickCateAdminService;
import com.bubblecloud.biz.oa.support.PhpResponse;
import com.bubblecloud.oa.api.entity.Category;
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
 * 快捷入口分类（对齐 PHP {@code ent/config/quickCate}）。
 *
 * @author qinlei
 * @date 2026/3/30
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/config/quickCate")
@Tag(name = "快捷入口分类")
public class QuickCateAdminController {

	private final QuickCateAdminService quickCateAdminService;

	@GetMapping
	@Operation(summary = "分类列表")
	public PhpResponse<?> index(@RequestParam(defaultValue = "0") int entid) {
		return PhpResponse.ok(quickCateAdminService.list(entid));
	}

	@GetMapping("/create")
	@Operation(summary = "创建表单占位")
	public PhpResponse<String> create() {
		return PhpResponse.ok("ok");
	}

	@PostMapping
	@Operation(summary = "新增分类")
	public PhpResponse<String> store(@RequestBody Category body) {
		quickCateAdminService.save(body);
		return PhpResponse.ok("common.insert.succ");
	}

	@GetMapping("/{id}/edit")
	@Operation(summary = "编辑数据")
	public PhpResponse<Category> edit(@PathVariable long id) {
		return PhpResponse.ok(quickCateAdminService.get(id));
	}

	@PutMapping("/{id}")
	@Operation(summary = "保存修改")
	public PhpResponse<String> update(@PathVariable long id, @RequestBody Category body) {
		body.setId(id);
		quickCateAdminService.update(body);
		return PhpResponse.ok("common.update.succ");
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除分类")
	public PhpResponse<String> destroy(@PathVariable long id) {
		quickCateAdminService.delete(id);
		return PhpResponse.ok("common.delete.succ");
	}

}
