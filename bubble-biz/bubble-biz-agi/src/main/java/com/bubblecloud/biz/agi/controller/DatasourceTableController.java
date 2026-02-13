package com.bubblecloud.biz.agi.controller;

import cn.hutool.core.util.StrUtil;
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
import com.bubblecloud.agi.api.entity.DatasourceTable;
import com.bubblecloud.biz.agi.service.DatasourceTableService;

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
 * 数据源授权
 *
 * @author Rampart
 * @date 2026-02-13 16:47:36
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/datasourceTable" )
@Tag(description = "datasourceTable" , name = "数据源授权管理" )
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class DatasourceTableController {

    private final  DatasourceTableService datasourceTableService;

    /**
     * 分页查询
     * @param pg 分页对象
     * @param req 数据源授权
     * @return
     */
    @Operation(summary = "分页查询" , description = "分页查询" )
    @GetMapping("/page" )
    @HasPermission("agi_datasourceTable_view")
    public R<Page<DatasourceTable>> getDatasourceTablePage(@ParameterObject Pg pg, @ParameterObject DatasourceTable req) {
       	pg.addOrderDefault(OrderItem.desc("t.id"));
		return R.ok(datasourceTableService.findPg(pg, req));
    }


    /**
     * 通过条件查询数据源授权
     * @param req 查询条件
     * @return R  对象列表
     */
    @Operation(summary = "通过条件查询" , description = "通过条件查询对象" )
    @GetMapping("/details" )
    @HasPermission("agi_datasourceTable_view")
    public R<List<DatasourceTable>> getDetails(@ParameterObject DatasourceTable req) {
        return R.ok(datasourceTableService.list(Wrappers.query(req)));
    }

    /**
     * 新增数据源授权
     * @param req 数据源授权
     * @return R
     */
    @Operation(summary = "新增数据源授权" , description = "新增数据源授权" )
    @SysLog("新增数据源授权" )
    @PostMapping
    @HasPermission("agi_datasourceTable_add")
    public R create(@RequestBody @Validated({Req.Create.class}) DatasourceTable req) {
        return R.ok(datasourceTableService.create(req));
    }

    /**
     * 修改数据源授权
     * @param req 数据源授权
     * @return R
     */
    @Operation(summary = "修改数据源授权" , description = "修改数据源授权" )
    @SysLog("修改数据源授权" )
    @PutMapping
    @HasPermission("agi_datasourceTable_edit")
    public R update(@RequestBody @Validated({Req.Update.class}) DatasourceTable req) {
        return R.ok(datasourceTableService.update(req));
    }

    /**
     * 通过id删除数据源授权
     * @param ids id列表
     * @return R
     */
    @Operation(summary = "通过id删除数据源授权" , description = "通过id删除数据源授权" )
    @SysLog("通过id删除数据源授权" )
    @DeleteMapping
    @HasPermission("agi_datasourceTable_del")
    public R removeById(@RequestBody Long[] ids) {
        return R.ok(datasourceTableService.removeBatchByIds(CollUtil.toList(ids)));
    }


    /**
     * 导出excel 表格
     * @param req 查询条件
   	 * @param ids 导出指定ID
     * @return excel 文件流
     */
	@Operation(summary = "导出", description = "导出")
    @ResponseExcel
    @GetMapping("/export")
    @HasPermission("agi_datasourceTable_export")
    public List<DatasourceTable> exportExcel(DatasourceTable req, Long[] ids) {
        return datasourceTableService.list(Wrappers.lambdaQuery(req).in(ArrayUtil.isNotEmpty(ids), DatasourceTable::getId, ids));
    }

    /**
     * 导入excel 表
     * @param datasourceTableList 对象实体列表
     * @param bindingResult 错误信息列表
     * @return ok fail
     */
	@Operation(summary = "导入", description = "导入")
    @PostMapping("/import")
    @HasPermission("agi_datasourceTable_export")
    public R importExcel(@RequestExcel List<DatasourceTable> datasourceTableList, BindingResult bindingResult) {
        return R.ok(datasourceTableService.saveBatch(datasourceTableList));
    }
}