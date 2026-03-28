package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.biz.oa.support.PhpResponse;
import com.bubblecloud.oa.api.vo.SimplePageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统配置扩展（占位，待按字典/表单等表实现）。
 */
@RestController
@RequestMapping("/ent/config")
@Tag(name = "系统配置扩展")
public class ConfigController {

	@GetMapping("/dict/type/page")
	@Operation(summary = "字典类型分页")
	public PhpResponse<SimplePageVO> dictTypePage(@RequestParam(defaultValue = "1") Integer current,
			@RequestParam(defaultValue = "20") Integer size) {
		return PhpResponse.ok(SimplePageVO.empty(current, size));
	}

	@GetMapping("/dict/data/page")
	@Operation(summary = "字典数据分页")
	public PhpResponse<SimplePageVO> dictDataPage(@RequestParam(defaultValue = "1") Integer current,
			@RequestParam(defaultValue = "20") Integer size) {
		return PhpResponse.ok(SimplePageVO.empty(current, size));
	}

	@GetMapping("/form/page")
	@Operation(summary = "表单分页")
	public PhpResponse<SimplePageVO> formPage(@RequestParam(defaultValue = "1") Integer current,
			@RequestParam(defaultValue = "20") Integer size) {
		return PhpResponse.ok(SimplePageVO.empty(current, size));
	}

}
