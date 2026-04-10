package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.common.core.util.R;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 开放 API（第十二节，W-27）：注册与 PHP 一致的关键路径，返回明确占位提示，避免 404。
 *
 * @author qinlei
 * @date 2026/4/6 16:30
 */
@RestController
@Tag(name = "开放API占位")
public class OpenApiWave7StubController {

	private static R<String> stub() {
		return R.phpOk("open_api_stub");
	}

	@Hidden
	@PostMapping("/open/auth/login")
	@Operation(summary = "开放授权登录（占位）")
	public R<String> openAuthLogin() {
		return stub();
	}

	@PostMapping("/open/bill/pay")
	@Operation(summary = "open bill（占位）")
	public R<String> openBillPay() {
		return stub();
	}

	@PostMapping("/open/customer/store")
	@Operation(summary = "open customer（占位）")
	public R<String> openCustomerStore() {
		return stub();
	}

	@GetMapping("/ent/openapi/key/list")
	@Operation(summary = "OpenAPI Key 列表（占位）")
	public R<String> openApiKeyListStub() {
		return stub();
	}

}
