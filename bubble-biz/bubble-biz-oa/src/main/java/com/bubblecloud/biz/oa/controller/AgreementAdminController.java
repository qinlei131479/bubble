package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.biz.oa.service.AgreementAdminService;
import com.bubblecloud.biz.oa.support.PhpResponse;
import com.bubblecloud.oa.api.entity.Agreement;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户协议（对齐 PHP {@code ent/system/treaty}）。
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
	public PhpResponse<?> index(@RequestParam(required = false) String title,
								@RequestParam(required = false) String ident) {
		return PhpResponse.ok(agreementAdminService.list(title, ident));
	}

	@GetMapping("/{id}/edit")
	@Operation(summary = "协议详情")
	public PhpResponse<Agreement> detail(@PathVariable Long id) {
		return PhpResponse.ok(agreementAdminService.getById(id));
	}

	@PutMapping("/{id}")
	@Operation(summary = "保存协议")
	public PhpResponse<String> update(@PathVariable Long id, @RequestBody Agreement body) {
		body.setId(id);
		agreementAdminService.update(body);
		return PhpResponse.ok("common.update.succ");
	}

}
