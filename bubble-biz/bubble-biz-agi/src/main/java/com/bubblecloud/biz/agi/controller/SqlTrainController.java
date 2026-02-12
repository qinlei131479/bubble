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
import com.bubblecloud.agi.api.entity.SqlTrain;
import com.bubblecloud.biz.agi.service.SqlTrainService;

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
 * SQL训练示例
 *
 * @author Rampart
 * @date 2026-02-12 18:37:03
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/sqlTrain" )
@Tag(description = "sqlTrain" , name = "SQL训练示例管理" )
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class SqlTrainController {

    private final  SqlTrainService sqlTrainService;

    /**
     * 分页查询
     * @param pg 分页对象
     * @param req SQL训练示例
     * @return
     */
    @Operation(summary = "分页查询" , description = "分页查询" )
    @GetMapping("/page" )
    @HasPermission("agi_sqlTrain_view")
    public R<Page<SqlTrain>> getSqlTrainPage(@ParameterObject Pg pg, @ParameterObject SqlTrain req) {
       	pg.addOrderDefault(OrderItem.desc("t.id"));
		return R.ok(sqlTrainService.findPg(pg, req));
    }


    /**
     * 通过条件查询SQL训练示例
     * @param req 查询条件
     * @return R  对象列表
     */
    @Operation(summary = "通过条件查询" , description = "通过条件查询对象" )
    @GetMapping("/details" )
    @HasPermission("agi_sqlTrain_view")
    public R<List<SqlTrain>> getDetails(@ParameterObject SqlTrain req) {
        return R.ok(sqlTrainService.list(Wrappers.query(req)));
    }

    /**
     * 新增SQL训练示例
     * @param req SQL训练示例
     * @return R
     */
    @Operation(summary = "新增SQL训练示例" , description = "新增SQL训练示例" )
    @SysLog("新增SQL训练示例" )
    @PostMapping
    @HasPermission("agi_sqlTrain_add")
    public R create(@RequestBody @Validated({Req.Create.class}) SqlTrain req) {
        return R.ok(sqlTrainService.create(req));
    }

    /**
     * 修改SQL训练示例
     * @param req SQL训练示例
     * @return R
     */
    @Operation(summary = "修改SQL训练示例" , description = "修改SQL训练示例" )
    @SysLog("修改SQL训练示例" )
    @PutMapping
    @HasPermission("agi_sqlTrain_edit")
    public R update(@RequestBody @Validated({Req.Update.class}) SqlTrain req) {
        return R.ok(sqlTrainService.update(req));
    }

    /**
     * 通过id删除SQL训练示例
     * @param ids id列表
     * @return R
     */
    @Operation(summary = "通过id删除SQL训练示例" , description = "通过id删除SQL训练示例" )
    @SysLog("通过id删除SQL训练示例" )
    @DeleteMapping
    @HasPermission("agi_sqlTrain_del")
    public R removeById(@RequestBody Long[] ids) {
        return R.ok(sqlTrainService.removeBatchByIds(CollUtil.toList(ids)));
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
    @HasPermission("agi_sqlTrain_export")
    public List<SqlTrain> exportExcel(SqlTrain req, Long[] ids) {
        return sqlTrainService.list(Wrappers.lambdaQuery(req).in(ArrayUtil.isNotEmpty(ids), SqlTrain::getId, ids));
    }

    /**
     * 导入excel 表
     * @param sqlTrainList 对象实体列表
     * @param bindingResult 错误信息列表
     * @return ok fail
     */
	@Operation(summary = "导入", description = "导入")
    @PostMapping("/import")
    @HasPermission("agi_sqlTrain_export")
    public R importExcel(@RequestExcel List<SqlTrain> sqlTrainList, BindingResult bindingResult) {
        return R.ok(sqlTrainService.saveBatch(sqlTrainList));
    }
}