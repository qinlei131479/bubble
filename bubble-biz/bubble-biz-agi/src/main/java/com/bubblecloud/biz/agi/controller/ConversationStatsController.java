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
import com.bubblecloud.agi.api.entity.ConversationStats;
import com.bubblecloud.biz.agi.service.ConversationStatsService;

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
 * 对话统计
 *
 * @author rampart
 * @date 2026-02-13 09:38:18
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/conversationStats")
@Tag(description = "conversationStats", name = "对话统计管理")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class ConversationStatsController {

	private final ConversationStatsService conversationStatsService;

	/**
	 * 分页查询
	 *
	 * @param pg  分页对象
	 * @param req 对话统计
	 * @return
	 */
	@Operation(summary = "分页查询", description = "分页查询")
	@GetMapping("/page")
	@HasPermission("agi_conversationStats_view")
	public R<Page<ConversationStats>> getConversationStatsPage(@ParameterObject Pg pg, @ParameterObject ConversationStats req) {
		pg.addOrderDefault(OrderItem.desc("t.id"));
		return R.ok(conversationStatsService.findPg(pg, req));
	}


	/**
	 * 通过条件查询对话统计
	 *
	 * @param req 查询条件
	 * @return R  对象列表
	 */
	@Operation(summary = "通过条件查询", description = "通过条件查询对象")
	@GetMapping("/details")
	@HasPermission("agi_conversationStats_view")
	public R<List<ConversationStats>> getDetails(@ParameterObject ConversationStats req) {
		return R.ok(conversationStatsService.list(Wrappers.query(req)));
	}

	/**
	 * 新增对话统计
	 *
	 * @param req 对话统计
	 * @return R
	 */
	@Operation(summary = "新增对话统计", description = "新增对话统计")
	@SysLog("新增对话统计")
	@PostMapping
	@HasPermission("agi_conversationStats_add")
	public R create(@RequestBody @Validated({Req.Create.class}) ConversationStats req) {
		return R.ok(conversationStatsService.create(req));
	}

	/**
	 * 修改对话统计
	 *
	 * @param req 对话统计
	 * @return R
	 */
	@Operation(summary = "修改对话统计", description = "修改对话统计")
	@SysLog("修改对话统计")
	@PutMapping
	@HasPermission("agi_conversationStats_edit")
	public R update(@RequestBody @Validated({Req.Update.class}) ConversationStats req) {
		return R.ok(conversationStatsService.update(req));
	}

	/**
	 * 通过id删除对话统计
	 *
	 * @param ids id列表
	 * @return R
	 */
	@Operation(summary = "通过id删除对话统计", description = "通过id删除对话统计")
	@SysLog("通过id删除对话统计")
	@DeleteMapping
	@HasPermission("agi_conversationStats_del")
	public R removeById(@RequestBody Long[] ids) {
		return R.ok(conversationStatsService.removeBatchByIds(CollUtil.toList(ids)));
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
	@HasPermission("agi_conversationStats_export")
	public List<ConversationStats> exportExcel(ConversationStats req, Long[] ids) {
		return conversationStatsService.list(Wrappers.lambdaQuery(req).in(ArrayUtil.isNotEmpty(ids), ConversationStats::getId, ids));
	}

	/**
	 * 导入excel 表
	 *
	 * @param conversationStatsList 对象实体列表
	 * @param bindingResult         错误信息列表
	 * @return ok fail
	 */
	@Operation(summary = "导入", description = "导入")
	@PostMapping("/import")
	@HasPermission("agi_conversationStats_export")
	public R importExcel(@RequestExcel List<ConversationStats> conversationStatsList, BindingResult bindingResult) {
		return R.ok(conversationStatsService.saveBatch(conversationStatsList));
	}
}