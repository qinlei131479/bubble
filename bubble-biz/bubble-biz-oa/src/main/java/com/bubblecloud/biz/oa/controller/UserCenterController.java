package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.biz.oa.support.PhpResponse;
import com.bubblecloud.oa.api.vo.SimplePageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户中心模块（Phase 9，占位：待按表实现 Mapper 与业务）。
 *
 * @author qinlei
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/user-center")
@Tag(name = "用户中心")
public class UserCenterController {

	@GetMapping("/resume/page")
	@Operation(summary = "用户简历分页")
	public PhpResponse<SimplePageVO> resumePage(@RequestParam(defaultValue = "1") Integer current,
												@RequestParam(defaultValue = "20") Integer size) {
		return PhpResponse.ok(SimplePageVO.empty(current, size));
	}

}
