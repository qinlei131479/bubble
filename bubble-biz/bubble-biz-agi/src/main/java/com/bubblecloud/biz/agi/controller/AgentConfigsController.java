package com.bubblecloud.biz.agi.controller;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.agi.api.entity.AgentConfigs;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.log.annotation.SysLog;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.common.mybatis.base.Req;
import com.bubblecloud.common.security.annotation.HasPermission;
import com.pig4cloud.plugin.excel.annotation.ResponseExcel;
import com.bubblecloud.biz.agi.service.AgentConfigsService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 智能体配置表
 *
 * @author rampart
 * @date 2026-02-11 10:56:22
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/agentConfigs")
@Tag(description = "agentConfigs", name = "智能体管理")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class AgentConfigsController {

	private final AgentConfigsService agentConfigsService;

	/**
	 * 分页查询
	 *
	 * @param pg  分页对象
	 * @param req 智能体配置表
	 * @return
	 */
	@Operation(summary = "分页查询", description = "分页查询")
	@GetMapping("/page")
	@HasPermission("agi_agentConfigs_view")
	public R<Page<AgentConfigs>> page(@ParameterObject Pg pg, @ParameterObject AgentConfigs req) {
		pg.addOrderDefault(OrderItem.desc("t.id"));
		return R.ok(agentConfigsService.findPg(pg, req));
	}


	/**
	 * 通过条件查询智能体
	 *
	 * @param req 查询条件
	 * @return R  对象列表
	 */
	@Operation(summary = "通过条件查询", description = "通过条件查询对象")
	@GetMapping("/details")
	@HasPermission("agi_agentConfigs_view")
	public R<List<AgentConfigs>> getDetails(@ParameterObject AgentConfigs req) {
		return R.ok(agentConfigsService.list(Wrappers.query(req)));
	}

	/**
	 * 新增智能体
	 *
	 * @param req 智能体
	 * @return R
	 */
	@Operation(summary = "新增智能体", description = "新增智能体")
	@SysLog("新增智能体")
	@PostMapping
	@HasPermission("agi_agentConfigs_add")
	public R create(@RequestBody @Validated({Req.Create.class}) AgentConfigs req) {
		return R.ok(agentConfigsService.create(req));
	}

	/**
	 * 修改智能体
	 *
	 * @param req 智能体
	 * @return R
	 */
	@Operation(summary = "修改智能体", description = "修改智能体")
	@SysLog("修改智能体")
	@PutMapping
	@HasPermission("agi_agentConfigs_edit")
	public R update(@RequestBody @Validated({Req.Update.class}) AgentConfigs req) {
		return R.ok(agentConfigsService.update(req));
	}

	/**
	 * 通过id删除智能体
	 *
	 * @param ids id列表
	 * @return R
	 */
	@Operation(summary = "通过id删除智能体", description = "通过id删除智能体")
	@SysLog("通过id删除智能体")
	@DeleteMapping
	@HasPermission("agi_agentConfigs_del")
	public R removeById(@RequestBody Long[] ids) {
		return R.ok(agentConfigsService.removeBatchByIds(CollUtil.toList(ids)));
	}


	/**
	 * 导出excel 表格
	 *
	 * @param req 查询条件
	 * @param ids 导出指定ID
	 * @return excel 文件流
	 */
	@Operation(summary = "导出", description = "导出")
	@ResponseExcel
	@GetMapping("/export")
	@HasPermission("agi_agentConfigs_export")
	public List<AgentConfigs> exportExcel(AgentConfigs req, Long[] ids) {
		return agentConfigsService.list(Wrappers.lambdaQuery(req).in(ArrayUtil.isNotEmpty(ids), AgentConfigs::getId, ids));
	}

}
