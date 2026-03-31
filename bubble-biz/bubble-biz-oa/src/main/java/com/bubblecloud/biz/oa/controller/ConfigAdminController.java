package com.bubblecloud.biz.oa.controller;

import java.util.List;

import com.bubblecloud.biz.oa.service.ConfigAdminService;
import com.bubblecloud.biz.oa.support.PhpResponse;
import com.bubblecloud.oa.api.dto.config.FirewallConfigSaveDTO;
import com.bubblecloud.oa.api.entity.SystemConfig;
import com.bubblecloud.oa.api.vo.config.ConfigCateItemVO;
import com.bubblecloud.oa.api.vo.config.FirewallConfigVO;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import cn.hutool.core.util.ObjectUtil;

/**
 * 企业系统配置（对齐 PHP {@code SysremConfigController} + {@code ConfigCateController}）。
 *
 * @author qinlei
 * @date 2026/3/30 下午9:20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/config")
@Tag(name = "企业系统配置")
public class ConfigAdminController {

	private final ConfigAdminService configAdminService;

	@GetMapping("/work_bench")
	@Operation(summary = "获取企业工作台配置数据")
	public PhpResponse<List<SystemConfig>> getWorkBenchFrom(@RequestParam(defaultValue = "1") int entid) {
		return PhpResponse.ok(configAdminService.listWorkBenchConfigs(entid));
	}

	@PostMapping("/work_bench")
	@Operation(summary = "保存企业工作台配置")
	public PhpResponse<String> saveWorkBenchFrom(@RequestParam(defaultValue = "1") int entid,
			@RequestBody JsonNode body) {
		configAdminService.saveWorkBench(entid, body);
		return PhpResponse.ok("保存配置成功");
	}

	@GetMapping("/data/updateConfig")
	@Operation(summary = "修改配置获取表单数据（配置行列表）")
	public PhpResponse<List<SystemConfig>> updateConfig(@RequestParam String category) {
		return PhpResponse.ok(configAdminService.listConfigRowsByCategory(category));
	}

	@PutMapping("/data/all/{category}")
	@Operation(summary = "按分类保存系统配置")
	public PhpResponse<String> updateAll(@PathVariable String category, @RequestBody JsonNode body) {
		configAdminService.updateAllByCategory(category, body);
		return PhpResponse.ok("保存成功");
	}

	@GetMapping("/data/firewall")
	@Operation(summary = "获取防火墙配置")
	public PhpResponse<FirewallConfigVO> firewallConfig() {
		return PhpResponse.ok(configAdminService.getFirewallConfig());
	}

	@PutMapping("/data/firewall")
	@Operation(summary = "保存防火墙配置")
	public PhpResponse<String> saveFirewallConfig(@RequestBody FirewallConfigSaveDTO dto) {
		int sw = ObjectUtil.isNotNull(dto.getFirewallSwitch()) ? dto.getFirewallSwitch() : 0;
		configAdminService.saveFirewallConfig(sw, dto.getFirewallContent());
		return PhpResponse.ok("保存成功");
	}

	@GetMapping("/cate")
	@Operation(summary = "配置分类列表")
	public PhpResponse<List<ConfigCateItemVO>> configCate() {
		return PhpResponse.ok(configAdminService.listConfigCateBase());
	}

}
