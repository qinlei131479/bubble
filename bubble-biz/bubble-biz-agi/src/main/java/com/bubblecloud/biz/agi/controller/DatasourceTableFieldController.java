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
import com.bubblecloud.agi.api.entity.DatasourceTableField;
import com.bubblecloud.biz.agi.service.DatasourceTableFieldService;

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
 * 数据源表字段
 *
 * @author Rampart
 * @date 2026-02-13 16:48:54
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/datasourceTableField" )
@Tag(description = "datasourceTableField" , name = "数据源表字段管理" )
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class DatasourceTableFieldController {

    private final  DatasourceTableFieldService datasourceTableFieldService;

    /**
     * 分页查询
     * @param pg 分页对象
     * @param req 数据源表字段
     * @return
     */
    @Operation(summary = "分页查询" , description = "分页查询" )
    @GetMapping("/page" )
    @HasPermission("agi_datasourceTableField_view")
    public R<Page<DatasourceTableField>> getDatasourceTableFieldPage(@ParameterObject Pg pg, @ParameterObject DatasourceTableField req) {
       	pg.addOrderDefault(OrderItem.desc("t.id"));
		return R.ok(datasourceTableFieldService.findPg(pg, req));
    }


    /**
     * 通过条件查询数据源表字段
     * @param req 查询条件
     * @return R  对象列表
     */
    @Operation(summary = "通过条件查询" , description = "通过条件查询对象" )
    @GetMapping("/details" )
    @HasPermission("agi_datasourceTableField_view")
    public R<List<DatasourceTableField>> getDetails(@ParameterObject DatasourceTableField req) {
        return R.ok(datasourceTableFieldService.list(Wrappers.query(req)));
    }

    /**
     * 新增数据源表字段
     * @param req 数据源表字段
     * @return R
     */
    @Operation(summary = "新增数据源表字段" , description = "新增数据源表字段" )
    @SysLog("新增数据源表字段" )
    @PostMapping
    @HasPermission("agi_datasourceTableField_add")
    public R create(@RequestBody @Validated({Req.Create.class}) DatasourceTableField req) {
        return R.ok(datasourceTableFieldService.create(req));
    }

    /**
     * 修改数据源表字段
     * @param req 数据源表字段
     * @return R
     */
    @Operation(summary = "修改数据源表字段" , description = "修改数据源表字段" )
    @SysLog("修改数据源表字段" )
    @PutMapping
    @HasPermission("agi_datasourceTableField_edit")
    public R update(@RequestBody @Validated({Req.Update.class}) DatasourceTableField req) {
        return R.ok(datasourceTableFieldService.update(req));
    }

    /**
     * 通过id删除数据源表字段
     * @param ids id列表
     * @return R
     */
    @Operation(summary = "通过id删除数据源表字段" , description = "通过id删除数据源表字段" )
    @SysLog("通过id删除数据源表字段" )
    @DeleteMapping
    @HasPermission("agi_datasourceTableField_del")
    public R removeById(@RequestBody Long[] ids) {
        return R.ok(datasourceTableFieldService.removeBatchByIds(CollUtil.toList(ids)));
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
    @HasPermission("agi_datasourceTableField_export")
    public List<DatasourceTableField> exportExcel(DatasourceTableField req, Long[] ids) {
        return datasourceTableFieldService.list(Wrappers.lambdaQuery(req).in(ArrayUtil.isNotEmpty(ids), DatasourceTableField::getId, ids));
    }

    /**
     * 导入excel 表
     * @param datasourceTableFieldList 对象实体列表
     * @param bindingResult 错误信息列表
     * @return ok fail
     */
	@Operation(summary = "导入", description = "导入")
    @PostMapping("/import")
    @HasPermission("agi_datasourceTableField_export")
    public R importExcel(@RequestExcel List<DatasourceTableField> datasourceTableFieldList, BindingResult bindingResult) {
        return R.ok(datasourceTableFieldService.saveBatch(datasourceTableFieldList));
    }
}