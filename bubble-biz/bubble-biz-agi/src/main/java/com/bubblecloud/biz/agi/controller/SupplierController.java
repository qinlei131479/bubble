package com.bubblecloud.biz.agi.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.common.security.annotation.HasPermission;
import com.pig4cloud.plugin.excel.annotation.ResponseExcel;
import io.swagger.v3.oas.annotations.Operation;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import com.baomidou.mybatisplus.core.metadata.OrderItem;

import lombok.RequiredArgsConstructor;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.base.Req;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.common.log.annotation.SysLog;
import com.bubblecloud.agi.api.entity.Supplier;
import com.bubblecloud.biz.agi.service.SupplierService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

/**
 * 控制器：AI供应商表
 *
 * @author Rampart Qin
 * @date 2026/02/11 18:33
 */
@RestController
@RequestMapping("/supplier")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
@Tag(description = "supplier", name = "供应商管理")
@RequiredArgsConstructor
public class SupplierController {

	private final SupplierService supplierService;

//	/**
//	 * 分页查询
//	 *
//	 * @param pg  分页对象
//	 * @param req 供应商
//	 * @return
//	 */
//	@Operation(summary = "分页查询", description = "分页查询说明")
//	@GetMapping("/page")
//	public R<Page<Supplier>> page(@ParameterObject Pg pg, @ParameterObject Supplier req) {
//		pg.addOrderDefault(OrderItem.desc("t.id"));
//		return R.ok(supplierService.findPg(pg, req));
//	}

	/**
	 * 通过条件查询
	 *
	 * @param req 查询条件
	 * @return R  对象列表
	 */
	@Operation(summary = "通过条件查询", description = "通过条件查询对象")
	@GetMapping("/details")
//	@HasPermission("agi_supplier_view")
	public R<List<Supplier>> getDetails(@ParameterObject Supplier req) {
		return R.ok(supplierService.list(Wrappers.query(req)));
	}


//	/**
//	 * 新增
//	 *
//	 * @param req 参数
//	 * @return R
//	 */
//	@Operation(summary = "添加", description = "")
//	@SysLog("添加供应商")
//	@PostMapping()
//	@HasPermission("agi_supplier_add")
//	public R create(@RequestBody @Validated({Req.Create.class}) Supplier req) {
//		return supplierService.create(req);
//	}
//
//	/**
//	 * 修改
//	 *
//	 * @param req 入参
//	 * @return R
//	 */
//	@Operation(summary = "编辑", description = "")
//	@SysLog("编辑供应商")
//	@PutMapping()
//	@HasPermission("agi_supplier_edit")
//	public R update(@RequestBody @Validated({Req.Update.class}) Supplier req) {
//		return supplierService.update(req);
//	}
//
//	/**
//	 * 通过id删除
//	 *
//	 * @param ids id列表
//	 * @return R
//	 */
//	@Operation(summary = "删除", description = "")
//	@DeleteMapping("")
//	@HasPermission("agi_supplier_del")
//	public R delete(@RequestBody Long[] ids) {
//		return R.ok(supplierService.removeBatchByIds(CollUtil.toList(ids)));
//	}
//
//	/**
//	 * 导出excel 表格
//	 *
//	 * @param req 查询条件
//	 * @param ids 导出指定ID
//	 * @return excel 文件流
//	 */
//	@Operation(summary = "导出", description = "导出")
//	@ResponseExcel
//	@GetMapping("/export")
//	@HasPermission("agi_supplier_export")
//	public List<Supplier> exportExcel(Supplier req, Long[] ids) {
//		return supplierService.list(Wrappers.lambdaQuery(req).in(ArrayUtil.isNotEmpty(ids), Supplier::getId, ids));
//	}

}
