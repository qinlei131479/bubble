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
 * 消息（占位，待按 eb_message 表实现）。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@RestController
@RequestMapping("/ent/message")
@Tag(name = "消息")
public class MessageController {

	@GetMapping("/page")
	@Operation(summary = "消息分页")
	public PhpResponse<SimplePageVO> page(@RequestParam(defaultValue = "1") Integer current,
			@RequestParam(defaultValue = "20") Integer size) {
		return PhpResponse.ok(SimplePageVO.empty(current, size));
	}

}
