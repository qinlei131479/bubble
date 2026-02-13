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
import com.bubblecloud.agi.api.entity.Datasource;
import com.bubblecloud.biz.agi.service.DatasourceService;

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
 * 数据源
 *
 * @author Rampart
 * @date 2026-02-13 16:45:56
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/datasource" )
@Tag(description = "datasource" , name = "数据源管理" )
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class DatasourceController {

    private final  DatasourceService datasourceService;

    /**
     * 分页查询
     * @param pg 分页对象
     * @param req 数据源
     * @return
     */
    @Operation(summary = "分页查询" , description = "分页查询" )
    @GetMapping("/page" )
    @HasPermission("agi_datasource_view")
    public R<Page<Datasource>> getDatasourcePage(@ParameterObject Pg pg, @ParameterObject Datasource req) {
       	pg.addOrderDefault(OrderItem.desc("t.id"));
		return R.ok(datasourceService.findPg(pg, req));
    }


    /**
     * 通过条件查询数据源
     * @param req 查询条件
     * @return R  对象列表
     */
    @Operation(summary = "通过条件查询" , description = "通过条件查询对象" )
    @GetMapping("/details" )
    @HasPermission("agi_datasource_view")
    public R<List<Datasource>> getDetails(@ParameterObject Datasource req) {
        return R.ok(datasourceService.list(Wrappers.query(req)));
    }

    /**
     * 新增数据源
     * @param req 数据源
     * @return R
     */
    @Operation(summary = "新增数据源" , description = "新增数据源" )
    @SysLog("新增数据源" )
    @PostMapping
    @HasPermission("agi_datasource_add")
    public R create(@RequestBody @Validated({Req.Create.class}) Datasource req) {
        return R.ok(datasourceService.create(req));
    }

    /**
     * 修改数据源
     * @param req 数据源
     * @return R
     */
    @Operation(summary = "修改数据源" , description = "修改数据源" )
    @SysLog("修改数据源" )
    @PutMapping
    @HasPermission("agi_datasource_edit")
    public R update(@RequestBody @Validated({Req.Update.class}) Datasource req) {
        return R.ok(datasourceService.update(req));
    }

    /**
     * 通过id删除数据源
     * @param ids id列表
     * @return R
     */
    @Operation(summary = "通过id删除数据源" , description = "通过id删除数据源" )
    @SysLog("通过id删除数据源" )
    @DeleteMapping
    @HasPermission("agi_datasource_del")
    public R removeById(@RequestBody Long[] ids) {
        return R.ok(datasourceService.removeBatchByIds(CollUtil.toList(ids)));
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
    @HasPermission("agi_datasource_export")
    public List<Datasource> exportExcel(Datasource req, Long[] ids) {
        return datasourceService.list(Wrappers.lambdaQuery(req).in(ArrayUtil.isNotEmpty(ids), Datasource::getId, ids));
    }

    /**
     * 导入excel 表
     * @param datasourceList 对象实体列表
     * @param bindingResult 错误信息列表
     * @return ok fail
     */
	@Operation(summary = "导入", description = "导入")
    @PostMapping("/import")
    @HasPermission("agi_datasource_export")
    public R importExcel(@RequestExcel List<Datasource> datasourceList, BindingResult bindingResult) {
        return R.ok(datasourceService.saveBatch(datasourceList));
    }
}