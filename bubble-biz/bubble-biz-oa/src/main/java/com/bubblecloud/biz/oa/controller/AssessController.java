package com.bubblecloud.biz.oa.controller;

import java.util.Map;

import com.bubblecloud.biz.oa.support.PhpResponse;
import com.bubblecloud.oa.api.vo.SimplePageVO;
import com.bubblecloud.oa.api.vo.assess.AssessWorkbenchIndexVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 绩效（占位，待按 eb_assess_scheme 等表实现）。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@RestController
@RequestMapping("/ent/assess")
@Tag(name = "绩效管理")
public class AssessController {

	@GetMapping("/page")
	@Operation(summary = "绩效方案分页")
	public PhpResponse<SimplePageVO> page(@RequestParam(defaultValue = "1") Integer current,
			@RequestParam(defaultValue = "20") Integer size) {
		return PhpResponse.ok(SimplePageVO.empty(current, size));
	}

	/**
	 * 工作台当前考核（PHP GET assess/index）。
	 */
	@GetMapping("/index")
	@Operation(summary = "考核列表")
	public PhpResponse<AssessWorkbenchIndexVO> index(@RequestParam Map<String, String> query) {
		return PhpResponse.ok(new AssessWorkbenchIndexVO());
	}

}
