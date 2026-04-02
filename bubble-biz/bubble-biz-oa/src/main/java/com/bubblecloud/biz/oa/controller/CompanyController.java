package com.bubblecloud.biz.oa.controller;

import java.util.Map;

import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.service.CompanyService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.dto.CompanyUpdateDTO;
import com.bubblecloud.oa.api.vo.SimplePageVO;
import com.bubblecloud.oa.api.vo.company.CompanyEntInfoVO;
import com.bubblecloud.oa.api.vo.company.CompanyMessageListVO;
import com.bubblecloud.oa.api.vo.company.CompanyQuantityVO;
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
 * 企业管理（对齐 PHP {@code ent/company}）。
 *
 * @author qinlei
 * @date 2026/3/29 下午5:30
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/company")
@Tag(name = "企业人事")
public class CompanyController {

	private final CompanyService companyService;

	@GetMapping("/info")
	@Operation(summary = "当前企业详情")
	public R<CompanyEntInfoVO> entInfo(@RequestParam(defaultValue = "1") int entid) {
		try {
			return R.phpOk(companyService.getEntAndUserInfo(entid));
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@PutMapping("/info")
	@Operation(summary = "修改当前企业详情")
	public R<String> updateEnt(@RequestParam(defaultValue = "1") int entid, @RequestBody CompanyUpdateDTO dto) {
		if (companyService.updateEnt(entid, dto)) {
			return R.phpOk(OaConstants.UPDATE_SUCC);
		}
		return R.phpFailed("common.update.fail");
	}

	@GetMapping("/quantity/{type}")
	@Operation(summary = "获取统计数量")
	public R<CompanyQuantityVO> quantity(@PathVariable String type, @RequestParam(defaultValue = "1") int entid) {
		return R.phpOk(companyService.getQuantity(type, entid));
	}

	@GetMapping("/user-card/page")
	@Operation(summary = "员工档案分页（占位）")
	public R<SimplePageVO> userCardPage(@RequestParam(defaultValue = "1") Integer current,
			@RequestParam(defaultValue = "20") Integer size) {
		return R.phpOk(SimplePageVO.empty(current, size));
	}

	@GetMapping("/message")
	@Operation(summary = "消息中心列表（工作台系统通知）")
	public R<CompanyMessageListVO> messageList(@RequestParam Map<String, String> query) {
		return R.phpOk(new CompanyMessageListVO());
	}

}
