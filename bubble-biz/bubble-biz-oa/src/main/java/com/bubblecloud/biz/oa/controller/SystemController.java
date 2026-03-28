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
 * 系统管理（占位，待按企业菜单/角色/日志等表实现）。
 *
 * @author qinlei
 */
@RestController
@RequestMapping("/ent/system")
@Tag(name = "系统管理")
public class SystemController {

	@GetMapping("/menu/page")
	@Operation(summary = "企业菜单分页")
	public PhpResponse<SimplePageVO> menuPage(@RequestParam(defaultValue = "1") Integer current,
											  @RequestParam(defaultValue = "20") Integer size) {
		return PhpResponse.ok(SimplePageVO.empty(current, size));
	}

	@GetMapping("/role/page")
	@Operation(summary = "企业角色分页")
	public PhpResponse<SimplePageVO> rolePage(@RequestParam(defaultValue = "1") Integer current,
											  @RequestParam(defaultValue = "20") Integer size) {
		return PhpResponse.ok(SimplePageVO.empty(current, size));
	}

	@GetMapping("/log/page")
	@Operation(summary = "操作日志分页")
	public PhpResponse<SimplePageVO> logPage(@RequestParam(defaultValue = "1") Integer current,
											 @RequestParam(defaultValue = "20") Integer size) {
		return PhpResponse.ok(SimplePageVO.empty(current, size));
	}

}
