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
import com.bubblecloud.agi.api.entity.SupplierModel;
import com.bubblecloud.biz.agi.service.SupplierModelService;

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
import java.util.Objects;

/**
 * AI供应商模型表
 *
 * @author Rampart
 * @date 2026-02-12 14:04:58
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/supplierModel")
@Tag(description = "supplierModel", name = "AI供应商模型表管理")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class SupplierModelController {

	private final SupplierModelService supplierModelService;

	/**
	 * 分页查询
	 *
	 * @param pg  分页对象
	 * @param req AI供应商模型表
	 * @return
	 */
	@Operation(summary = "分页查询", description = "分页查询")
	@GetMapping("/page")
	@HasPermission("agi_supplierModel_view")
	public R<Page<SupplierModel>> getSupplierModelPage(@ParameterObject Pg pg, @ParameterObject SupplierModel req) {
		pg.addOrderDefault(OrderItem.desc("t.id"));
		return R.ok(supplierModelService.findPg(pg, req));
	}


	/**
	 * 通过条件查询AI供应商模型表
	 *
	 * @param req 查询条件
	 * @return R  对象列表
	 */
	@Operation(summary = "通过条件查询", description = "通过条件查询对象")
	@GetMapping("/details")
	@HasPermission("agi_supplierModel_view")
	public R<List<SupplierModel>> getDetails(@ParameterObject SupplierModel req) {
		return R.ok(supplierModelService.list(Wrappers.query(req)));
	}

	/**
	 * 新增AI供应商模型表
	 *
	 * @param req AI供应商模型表
	 * @return R
	 */
	@Operation(summary = "新增AI供应商模型表", description = "新增AI供应商模型表")
	@SysLog("新增AI供应商模型表")
	@PostMapping
	@HasPermission("agi_supplierModel_add")
	public R create(@RequestBody @Validated({Req.Create.class}) SupplierModel req) {
		return R.ok(supplierModelService.create(req));
	}

	/**
	 * 修改AI供应商模型表
	 *
	 * @param req AI供应商模型表
	 * @return R
	 */
	@Operation(summary = "修改AI供应商模型表", description = "修改AI供应商模型表")
	@SysLog("修改AI供应商模型表")
	@PutMapping
	@HasPermission("agi_supplierModel_edit")
	public R update(@RequestBody @Validated({Req.Update.class}) SupplierModel req) {
		return R.ok(supplierModelService.update(req));
	}

	/**
	 * 通过id删除AI供应商模型表
	 *
	 * @param ids id列表
	 * @return R
	 */
	@Operation(summary = "通过id删除AI供应商模型表", description = "通过id删除AI供应商模型表")
	@SysLog("通过id删除AI供应商模型表")
	@DeleteMapping
	@HasPermission("agi_supplierModel_del")
	public R removeById(@RequestBody Long[] ids) {
		return R.ok(supplierModelService.removeBatchByIds(CollUtil.toList(ids)));
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
	@HasPermission("agi_supplierModel_export")
	public List<SupplierModel> exportExcel(SupplierModel req, Long[] ids) {
		return supplierModelService.list(Wrappers.lambdaQuery(req).in(ArrayUtil.isNotEmpty(ids), SupplierModel::getId, ids));
	}

	/**
	 * 导入excel 表
	 *
	 * @param supplierModelList 对象实体列表
	 * @param bindingResult     错误信息列表
	 * @return ok fail
	 */
	@Operation(summary = "导入", description = "导入")
	@PostMapping("/import")
	@HasPermission("agi_supplierModel_export")
	public R importExcel(@RequestExcel List<SupplierModel> supplierModelList, BindingResult bindingResult) {
		return R.ok(supplierModelService.saveBatch(supplierModelList));
	}
}