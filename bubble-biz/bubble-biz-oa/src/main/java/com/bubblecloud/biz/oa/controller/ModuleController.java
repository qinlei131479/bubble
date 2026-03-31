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
 * 低代码（占位，待按 eb_system_crud 等表实现）。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@RestController
@RequestMapping("/ent/module")
@Tag(name = "低代码")
public class ModuleController {

	@GetMapping("/crud/page")
	@Operation(summary = "动态表单分页")
	public PhpResponse<SimplePageVO> crudPage(@RequestParam(defaultValue = "1") Integer current,
			@RequestParam(defaultValue = "20") Integer size) {
		return PhpResponse.ok(SimplePageVO.empty(current, size));
	}

}
