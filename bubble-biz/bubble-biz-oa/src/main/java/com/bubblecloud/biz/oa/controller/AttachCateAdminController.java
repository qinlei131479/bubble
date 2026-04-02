package com.bubblecloud.biz.oa.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.service.AttachCateAdminService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.entity.Category;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

	private static final String TYPE_ATTACH = "systemAttach";

	private final AttachCateAdminService attachCateAdminService;

	@GetMapping
	@Operation(summary = "附件分类列表")
	public R<?> list(@RequestParam(defaultValue = "0") int entid) {
		return R.phpOk(attachCateAdminService.list(Wrappers.lambdaQuery(Category.class)
				.eq(Category::getType, TYPE_ATTACH)
				.eq(Category::getEntid, entid)
				.orderByDesc(Category::getSort)));
	}

}
