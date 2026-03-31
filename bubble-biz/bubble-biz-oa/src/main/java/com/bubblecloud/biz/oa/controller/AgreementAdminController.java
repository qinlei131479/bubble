package com.bubblecloud.biz.oa.controller;

import cn.hutool.core.collection.CollUtil;
import com.bubblecloud.biz.oa.service.AgreementAdminService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.entity.Agreement;
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
 * 用户协议（对齐 PHP {@code ent/system/treaty}）。增删改查路由与调用方式以本类为 OA 模板。
 *
 * @author qinlei
 * @date 2026/3/30
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/system/treaty")
@Tag(name = "用户协议")
public class AgreementAdminController {

	private final AgreementAdminService agreementAdminService;

	@GetMapping
	@Operation(summary = "协议列表")
	public R<?> list(@RequestParam(required = false) String title, @RequestParam(required = false) String ident) {
		return R.phpOk(agreementAdminService.list(title, ident));
	}

	@GetMapping("/{id}/edit")
	@Operation(summary = "协议详情")
	public R<Agreement> details(@PathVariable Long id) {
		return R.phpOk(agreementAdminService.getById(id));
	}

	@PostMapping
	@Operation(summary = "新增协议")
	public R<String> create(@RequestBody Agreement req) {
		agreementAdminService.create(req);
		return R.phpOk("common.insert.succ");
	}

	@PutMapping("/{id}")
	@Operation(summary = "保存协议（修改）")
	public R<String> update(@PathVariable Long id, @RequestBody Agreement req) {
		req.setId(id);
		agreementAdminService.update(req);
		return R.phpOk("common.update.succ");
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除协议")
	public R<?> removeById(@PathVariable Long id) {
		return R.phpOk(agreementAdminService.removeBatchByIds(CollUtil.toList(id)));
	}

}
