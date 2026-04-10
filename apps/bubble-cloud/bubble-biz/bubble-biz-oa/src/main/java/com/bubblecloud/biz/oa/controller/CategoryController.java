package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.service.CategoryService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.entity.Category;
import com.bubblecloud.oa.api.vo.form.OaElFormVO;
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
public class CategoryController {

	private final CategoryService categoryService;

	@GetMapping
	@Operation(summary = "分类列表")
	public R<?> list(@RequestParam(defaultValue = "0") Long entid) {
		return R.phpOk(categoryService.list(entid));
	}

	@GetMapping("/create")
	@Operation(summary = "添加快捷分类表单（elForm）")
	public R<OaElFormVO> createForm(@RequestParam(defaultValue = "1") Long entid) {
		return R.phpOk(categoryService.buildQuickCategoryCreateForm(entid));
	}

	@PostMapping
	@Operation(summary = "新增分类")
	public R<String> create(@RequestBody Category req) {
		categoryService.create(req);
		return R.phpOk(OaConstants.INSERT_SUCC);
	}

	@GetMapping("/{id}/edit")
	@Operation(summary = "编辑数据")
	public R<Category> details(@PathVariable Long id) {
		return R.phpOk(categoryService.getById(id));
	}

	@PutMapping("/{id}")
	@Operation(summary = "保存修改")
	public R<String> update(@PathVariable Long id, @RequestBody Category req) {
		req.setId(id);
		categoryService.update(req);
		return R.phpOk(OaConstants.UPDATE_SUCC);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除分类")
	public R<String> removeById(@PathVariable Long id) {
		categoryService.deleteById(id);
		return R.phpOk(OaConstants.DELETE_SUCC);
	}

}
