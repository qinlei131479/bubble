package com.bubblecloud.biz.oa.controller;

import java.util.List;

import com.bubblecloud.biz.oa.service.ClientRuleService;
import com.bubblecloud.biz.oa.support.PhpResponse;
import com.bubblecloud.oa.api.dto.config.ClientRuleApproveSaveDTO;
import com.bubblecloud.oa.api.vo.config.ClientRuleApproveConfigVO;
import com.bubblecloud.oa.api.vo.config.ConfigCateItemVO;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cn.hutool.core.util.StrUtil;

/**
 * 客户规则配置，对齐 PHP {@code ent/config/client_rule}。
 *
 * @author qinlei
 * @date 2026/3/30 下午10:22
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/config/client_rule")
@Tag(name = "客户规则配置")
public class ClientRuleController {

	private final ClientRuleService clientRuleService;

	@GetMapping("/cate")
	@Operation(summary = "客户规则分类列表")
	public PhpResponse<List<ConfigCateItemVO>> cateList() {
		return PhpResponse.ok(clientRuleService.listClientRuleCates());
	}

	@GetMapping({ "/approve", "/approve/{form}" })
	@Operation(summary = "获取客户审批规则")
	public PhpResponse<ClientRuleApproveConfigVO> getApproveConfig(@PathVariable(required = false) Integer form) {
		return PhpResponse.ok(clientRuleService.getApproveConfig(form));
	}

	@PutMapping("/approve")
	@Operation(summary = "保存客户审批规则")
	public PhpResponse<String> setApproveConfig(@RequestBody ClientRuleApproveSaveDTO dto) {
		clientRuleService.saveApproveConfig(dto);
		return PhpResponse.ok("保存成功");
	}

	@GetMapping("/{category}")
	@Operation(summary = "按分类读取配置为 JSON 对象")
	public PhpResponse<JsonNode> getConfig(@PathVariable String category) {
		if (StrUtil.isBlank(category)) {
			return PhpResponse.failed("common.empty.attrs");
		}
		return PhpResponse.ok(clientRuleService.getConfigByCategory(category));
	}

	@PutMapping("/{category}")
	@Operation(summary = "按分类保存配置")
	public PhpResponse<String> setConfig(@PathVariable String category, @RequestBody JsonNode body) {
		if (StrUtil.isBlank(category)) {
			return PhpResponse.failed("common.empty.attrs");
		}
		clientRuleService.saveConfigByCategory(category, body);
		return PhpResponse.ok("保存成功");
	}

}
