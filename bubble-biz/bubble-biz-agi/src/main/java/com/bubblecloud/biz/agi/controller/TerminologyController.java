package com.bubblecloud.biz.agi.controller;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.collection.CollUtil;

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
import com.bubblecloud.agi.api.entity.Terminology;
import com.bubblecloud.biz.agi.service.TerminologyService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

/**
 * 控制器：术语表
 *
 * @author Rampart Qin
 * @date   2026/02/11 22:35
 */
@RestController
@RequestMapping("/terminology")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
@Tag(description = "terminology", name = "术语管理")
@RequiredArgsConstructor
public class TerminologyController {

	private final TerminologyService terminologyService;

    /**
     * 分页查询
     *
     * @param pg  分页对象
     * @param req 查询参数对象
     * @return
     */
    @Operation(summary = "分页查询", description = "分页查询说明")
    @GetMapping("/page")
    @HasPermission("agi_terminology_view")
    public R<Page<Terminology>> page(@ParameterObject Pg pg, @ParameterObject Terminology req) {
        pg.addOrderDefault(OrderItem.desc("t.id"));
        return R.ok(terminologyService.findPg(pg, req));
    }

    /**
     * 通过条件查询
     *
     * @param req 查询条件
     * @return R  对象列表
     */
    @Operation(summary = "通过条件查询", description = "通过条件查询对象")
    @GetMapping("/details")
    @HasPermission("agi_terminology_view")
    public R<List<Terminology>> getDetails(@ParameterObject Terminology req) {
        return R.ok(terminologyService.list(Wrappers.query(req)));
    }

    /**
	 * 新增术语表
	 *
	 * @param req 术语表
	 * @return R
	 */
	@Operation(summary = "新增术语", description = "新增术语")
	@SysLog("新增术语")
	@PostMapping
	@HasPermission("agi_terminology_add")
	public R create(@RequestBody @Validated({Req.Create.class}) Terminology req) {
		return R.ok(terminologyService.create(req));
	}

	/**
	 * 修改
	 *
	 * @param req 术语表
	 * @return R
	 */
	@Operation(summary = "修改术语", description = "修改术语")
	@SysLog("修改术语")
	@PutMapping
	@HasPermission("agi_terminology_edit")
	public R update(@RequestBody @Validated({Req.Update.class}) Terminology req) {
		return R.ok(terminologyService.update(req));
	}

	/**
	 * 通过id删除
	 *
	 * @param ids id列表
	 * @return R
	 */
	@Operation(summary = "通过id删除", description = "通过id删除")
	@SysLog("通过id删除")
	@DeleteMapping
	@HasPermission("agi_terminology_del")
	public R removeById(@RequestBody Long[] ids) {
		return R.ok(terminologyService.removeBatchByIds(CollUtil.toList(ids)));
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
	@HasPermission("agi_terminology_export")
	public List<Terminology> exportExcel(Terminology req, Long[] ids) {
		return terminologyService.list(Wrappers.lambdaQuery(req).in(ArrayUtil.isNotEmpty(ids), Terminology::getId, ids));
	}

}
