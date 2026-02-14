package com.bubblecloud.biz.agi.controller;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.common.mybatis.base.Req;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.log.annotation.SysLog;
import com.pig4cloud.plugin.excel.annotation.ResponseExcel;
import com.pig4cloud.plugin.excel.annotation.RequestExcel;
import com.bubblecloud.agi.api.entity.McpServers;
import com.bubblecloud.biz.agi.service.McpServersService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import com.bubblecloud.common.security.annotation.HasPermission;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * MCP服务表
 *
 * @author Rampart
 * @date 2026-02-12 18:18:39
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/mcpServers")
@Tag(description = "mcpServers", name = "MCP服务管理")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class McpServersController {

	private final McpServersService mcpServersService;

	/**
	 * 分页查询
	 *
	 * @param pg  分页对象
	 * @param req MCP服务表
	 * @return
	 */
	@Operation(summary = "分页查询", description = "分页查询")
	@GetMapping("/page")
	@HasPermission("agi_mcpServers_view")
	public R<Page<McpServers>> getMcpServersPage(@ParameterObject Pg pg, @ParameterObject McpServers req) {
		pg.addOrderDefault(OrderItem.desc("t.id"));
		return R.ok(mcpServersService.findPg(pg, req));
	}


	/**
	 * 通过条件查询MCP服务表
	 *
	 * @param req 查询条件
	 * @return R  对象列表
	 */
	@Operation(summary = "通过条件查询", description = "通过条件查询对象")
	@GetMapping("/details")
	@HasPermission("agi_mcpServers_view")
	public R<List<McpServers>> getDetails(@ParameterObject McpServers req) {
		return R.ok(mcpServersService.list(Wrappers.query(req)));
	}

	/**
	 * 新增MCP服务表
	 *
	 * @param req MCP服务表
	 * @return R
	 */
	@Operation(summary = "新增MCP服务", description = "新增MCP服务")
	@SysLog("新增MCP服务表")
	@PostMapping
	@HasPermission("agi_mcpServers_add")
	public R create(@RequestBody @Validated({Req.Create.class}) McpServers req) {
		return R.ok(mcpServersService.create(req));
	}

	/**
	 * 修改MCP服务表
	 *
	 * @param req MCP服务表
	 * @return R
	 */
	@Operation(summary = "修改MCP服务", description = "修改MCP服务")
	@SysLog("修改MCP服务")
	@PutMapping
	@HasPermission("agi_mcpServers_edit")
	public R update(@RequestBody @Validated({Req.Update.class}) McpServers req) {
		return R.ok(mcpServersService.update(req));
	}

	/**
	 * 通过id删除MCP服务表
	 *
	 * @param ids id列表
	 * @return R
	 */
	@Operation(summary = "通过id删除MCP服务", description = "通过id删除MCP服务")
	@SysLog("通过id删除MCP服务")
	@DeleteMapping
	@HasPermission("agi_mcpServers_del")
	public R removeById(@RequestBody Long[] ids) {
		return R.ok(mcpServersService.removeBatchByIds(CollUtil.toList(ids)));
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
	@HasPermission("agi_mcpServers_export")
	public List<McpServers> exportExcel(McpServers req, Long[] ids) {
		return mcpServersService.list(Wrappers.lambdaQuery(req).in(ArrayUtil.isNotEmpty(ids), McpServers::getId, ids));
	}

//	/**
//	 * 导入excel 表
//	 *
//	 * @param mcpServersList 对象实体列表
//	 * @param bindingResult  错误信息列表
//	 * @return ok fail
//	 */
//	@Operation(summary = "导入", description = "导入")
//	@PostMapping("/import")
//	@HasPermission("agi_mcpServers_export")
//	public R importExcel(@RequestExcel List<McpServers> mcpServersList, BindingResult bindingResult) {
//		return R.ok(mcpServersService.saveBatch(mcpServersList));
//	}
}