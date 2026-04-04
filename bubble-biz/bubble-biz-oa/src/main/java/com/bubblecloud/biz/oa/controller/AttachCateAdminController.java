package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.service.AttachCateAdminService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.entity.Category;
import com.bubblecloud.oa.api.vo.CategoryAttachTreeVO;
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
import cn.hutool.core.util.ObjectUtil;

/**
 * 附件分类（对齐 PHP {@code ent/system/attach_cate}，eb_category.type=systemAttach）。
 *
 * @author qinlei
 * @date 2026/3/30
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/system/attach_cate")
@Tag(name = "附件分类")
public class AttachCateAdminController {

	private final AttachCateAdminService attachCateAdminService;

	private static int entidOr1(Integer v) {
		return ObjectUtil.defaultIfNull(v, 1);
	}

	@GetMapping
	@Operation(summary = "附件分类树")
	public R<java.util.List<CategoryAttachTreeVO>> index(@RequestParam(defaultValue = "1") Integer entid) {
		return R.phpOk(attachCateAdminService.attachCateTree(entidOr1(entid)));
	}

	@GetMapping("/create")
	@Operation(summary = "添加表单（PHP 为表单构建器，此处占位）")
	public R<String> createForm() {
		return R.phpOk("ok");
	}

	@PostMapping
	@Operation(summary = "保存附件分类")
	public R<String> create(@RequestBody Category req, @RequestParam(defaultValue = "1") Integer entid) {
		req.setEntid(entidOr1(entid));
		attachCateAdminService.create(req);
		return R.phpOk(OaConstants.INSERT_SUCC);
	}

	@GetMapping("/{id}/edit")
	@Operation(summary = "编辑数据")
	public R<Category> edit(@PathVariable Long id, @RequestParam(defaultValue = "1") Integer entid) {
		Category row = attachCateAdminService.getById(id);
		if (ObjectUtil.isNull(row) || row.getEntid() == null || row.getEntid() != entidOr1(entid)) {
			return R.phpFailed("没有查询到数据！");
		}
		return R.phpOk(row);
	}

	@PutMapping("/{id}")
	@Operation(summary = "更新附件分类")
	public R<String> update(@PathVariable Long id, @RequestBody Category req,
			@RequestParam(defaultValue = "1") Integer entid) {
		Category row = attachCateAdminService.getById(id);
		if (ObjectUtil.isNull(row) || row.getEntid() == null || row.getEntid() != entidOr1(entid)) {
			return R.phpFailed("没有查询到数据！");
		}
		req.setId(id);
		req.setEntid(entidOr1(entid));
		attachCateAdminService.update(req);
		return R.phpOk(OaConstants.UPDATE_SUCC);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除附件分类")
	public R<String> remove(@PathVariable Long id, @RequestParam(defaultValue = "1") Integer entid) {
		attachCateAdminService.deleteAttachCategory(id, entidOr1(entid));
		return R.phpOk(OaConstants.DELETE_SUCC);
	}

}
