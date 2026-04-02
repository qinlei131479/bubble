package com.bubblecloud.biz.oa.controller;

import java.util.ArrayList;
import java.util.List;

import com.bubblecloud.biz.oa.service.SystemConfigService;
import com.bubblecloud.common.core.util.R;
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

	private final SystemConfigService systemConfigService;

	@GetMapping("/cate")
	@Operation(summary = "客户规则分类列表")
	public R<List<ConfigCateItemVO>> cateList() {
		List<ConfigCateItemVO> list = new ArrayList<>(3);
		list.add(new ConfigCateItemVO("客户跟进配置", "customer_follow_config"));
		list.add(new ConfigCateItemVO("客户公海配置", "customer_sea_config"));
		list.add(new ConfigCateItemVO("客户审批配置", "customer_approve_config"));
		return R.phpOk(list);
	}

	@GetMapping({ "/approve", "/approve/{form}" })
	@Operation(summary = "获取客户审批规则")
	public R<ClientRuleApproveConfigVO> getApproveConfig(@PathVariable(required = false) Integer form) {
		return R.phpOk(systemConfigService.getApproveConfig(form));
	}

	@PutMapping("/approve")
	@Operation(summary = "保存客户审批规则")
	public R<String> setApproveConfig(@RequestBody ClientRuleApproveSaveDTO dto) {
		systemConfigService.saveApproveConfig(dto);
		return R.phpOk("保存成功");
	}

	@GetMapping("/{category}")
	@Operation(summary = "按分类读取配置为 JSON 对象")
	public R<JsonNode> getConfig(@PathVariable String category) {
		return R.phpOk(systemConfigService.getConfigByCategory(category));
	}

	@PutMapping("/{category}")
	@Operation(summary = "按分类保存配置")
	public R<String> setConfig(@PathVariable String category, @RequestBody JsonNode body) {
		systemConfigService.saveConfigByCategory(category, body);
		return R.phpOk("保存成功");
	}

}
