package com.bubblecloud.biz.agi.controller;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.agi.api.entity.AgentConfigs;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.log.annotation.SysLog;
import com.bubblecloud.common.security.annotation.HasPermission;
import com.pig4cloud.plugin.excel.annotation.ResponseExcel;
import com.pig4cloud.plugin.excel.annotation.RequestExcel;
import com.bubblecloud.biz.agi.service.AgentConfigsService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * 智能体配置表
 *
 * @author rampart
 * @date 2026-02-11 10:56:22
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/agentConfigs")
@Tag(description = "agentConfigs", name = "智能体配置表管理")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class AgentConfigsController {

	private final AgentConfigsService agentConfigsService;

	/**
	 * 分页查询
	 *
	 * @param page         分页对象
	 * @param agentConfigs 智能体配置表
	 * @return
	 */
	@Operation(summary = "分页查询", description = "分页查询")
	@GetMapping("/page")
	@HasPermission("agi_agentConfigs_view")
	public R getAgentConfigsPage(@ParameterObject Page page, @ParameterObject AgentConfigs agentConfigs) {
		LambdaQueryWrapper<AgentConfigs> wrapper = Wrappers.lambdaQuery();
		return R.ok(agentConfigsService.page(page, wrapper));
	}


	/**
	 * 通过条件查询智能体配置表
	 *
	 * @param agentConfigs 查询条件
	 * @return R  对象列表
	 */
	@Operation(summary = "通过条件查询", description = "通过条件查询对象")
	@GetMapping("/details")
	@HasPermission("agi_agentConfigs_view")
	public R getDetails(@ParameterObject AgentConfigs agentConfigs) {
		return R.ok(agentConfigsService.list(Wrappers.query(agentConfigs)));
	}

	/**
	 * 新增智能体配置表
	 *
	 * @param agentConfigs 智能体配置表
	 * @return R
	 */
	@Operation(summary = "新增智能体配置表", description = "新增智能体配置表")
	@SysLog("新增智能体配置表")
	@PostMapping
	@HasPermission("agi_agentConfigs_add")
	public R save(@RequestBody AgentConfigs agentConfigs) {
		return R.ok(agentConfigsService.save(agentConfigs));
	}

	/**
	 * 修改智能体配置表
	 *
	 * @param agentConfigs 智能体配置表
	 * @return R
	 */
	@Operation(summary = "修改智能体配置表", description = "修改智能体配置表")
	@SysLog("修改智能体配置表")
	@PutMapping
	@HasPermission("agi_agentConfigs_edit")
	public R updateById(@RequestBody AgentConfigs agentConfigs) {
		return R.ok(agentConfigsService.updateById(agentConfigs));
	}

	/**
	 * 通过id删除智能体配置表
	 *
	 * @param ids id列表
	 * @return R
	 */
	@Operation(summary = "通过id删除智能体配置表", description = "通过id删除智能体配置表")
	@SysLog("通过id删除智能体配置表")
	@DeleteMapping
	@HasPermission("agi_agentConfigs_del")
	public R removeById(@RequestBody Long[] ids) {
		return R.ok(agentConfigsService.removeBatchByIds(CollUtil.toList(ids)));
	}


	/**
	 * 导出excel 表格
	 *
	 * @param agentConfigs 查询条件
	 * @param ids          导出指定ID
	 * @return excel 文件流
	 */
	@ResponseExcel
	@GetMapping("/export")
	@HasPermission("agi_agentConfigs_export")
	public List<AgentConfigs> exportExcel(AgentConfigs agentConfigs, Long[] ids) {
		return agentConfigsService.list(Wrappers.lambdaQuery(agentConfigs).in(ArrayUtil.isNotEmpty(ids), AgentConfigs::getId, ids));
	}

	/**
	 * 导入excel 表
	 *
	 * @param agentConfigsList 对象实体列表
	 * @param bindingResult    错误信息列表
	 * @return ok fail
	 */
	@PostMapping("/import")
	@HasPermission("agi_agentConfigs_export")
	public R importExcel(@RequestExcel List<AgentConfigs> agentConfigsList, BindingResult bindingResult) {
		return R.ok(agentConfigsService.saveBatch(agentConfigsList));
	}
}
