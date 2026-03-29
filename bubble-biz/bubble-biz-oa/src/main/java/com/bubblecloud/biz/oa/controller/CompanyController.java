package com.bubblecloud.biz.oa.controller;

import java.util.Map;

import com.bubblecloud.biz.oa.support.PhpResponse;
import com.bubblecloud.oa.api.vo.SimplePageVO;
import com.bubblecloud.oa.api.vo.company.CompanyMessageListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 企业与人事、企业消息（占位）。
 *
 * @author qinlei
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/company")
@Tag(name = "企业人事")
public class CompanyController {

	@GetMapping("/user-card/page")
	@Operation(summary = "员工档案分页")
	public PhpResponse<SimplePageVO> userCardPage(@RequestParam(defaultValue = "1") Integer current,
			@RequestParam(defaultValue = "20") Integer size) {
		return PhpResponse.ok(SimplePageVO.empty(current, size));
	}

	@GetMapping("/message")
	@Operation(summary = "消息中心列表（工作台系统通知）")
	public PhpResponse<CompanyMessageListVO> messageList(@RequestParam Map<String, String> query) {
		return PhpResponse.ok(new CompanyMessageListVO());
	}

}
