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
import com.bubblecloud.agi.api.entity.EvaluationBenchmarks;
import com.bubblecloud.biz.agi.service.EvaluationBenchmarksService;

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
 * 评估基准
 *
 * @author Rampart
 * @date 2026-02-13 16:51:12
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/evaluationBenchmarks" )
@Tag(description = "evaluationBenchmarks" , name = "评估基准管理" )
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class EvaluationBenchmarksController {

    private final  EvaluationBenchmarksService evaluationBenchmarksService;

    /**
     * 分页查询
     * @param pg 分页对象
     * @param req 评估基准
     * @return
     */
    @Operation(summary = "分页查询" , description = "分页查询" )
    @GetMapping("/page" )
    @HasPermission("agi_evaluationBenchmarks_view")
    public R<Page<EvaluationBenchmarks>> getEvaluationBenchmarksPage(@ParameterObject Pg pg, @ParameterObject EvaluationBenchmarks req) {
       	pg.addOrderDefault(OrderItem.desc("t.id"));
		return R.ok(evaluationBenchmarksService.findPg(pg, req));
    }


    /**
     * 通过条件查询评估基准
     * @param req 查询条件
     * @return R  对象列表
     */
    @Operation(summary = "通过条件查询" , description = "通过条件查询对象" )
    @GetMapping("/details" )
    @HasPermission("agi_evaluationBenchmarks_view")
    public R<List<EvaluationBenchmarks>> getDetails(@ParameterObject EvaluationBenchmarks req) {
        return R.ok(evaluationBenchmarksService.list(Wrappers.query(req)));
    }

    /**
     * 新增评估基准
     * @param req 评估基准
     * @return R
     */
    @Operation(summary = "新增评估基准" , description = "新增评估基准" )
    @SysLog("新增评估基准" )
    @PostMapping
    @HasPermission("agi_evaluationBenchmarks_add")
    public R create(@RequestBody @Validated({Req.Create.class}) EvaluationBenchmarks req) {
        return R.ok(evaluationBenchmarksService.create(req));
    }

    /**
     * 修改评估基准
     * @param req 评估基准
     * @return R
     */
    @Operation(summary = "修改评估基准" , description = "修改评估基准" )
    @SysLog("修改评估基准" )
    @PutMapping
    @HasPermission("agi_evaluationBenchmarks_edit")
    public R update(@RequestBody @Validated({Req.Update.class}) EvaluationBenchmarks req) {
        return R.ok(evaluationBenchmarksService.update(req));
    }

    /**
     * 通过id删除评估基准
     * @param ids id列表
     * @return R
     */
    @Operation(summary = "通过id删除评估基准" , description = "通过id删除评估基准" )
    @SysLog("通过id删除评估基准" )
    @DeleteMapping
    @HasPermission("agi_evaluationBenchmarks_del")
    public R removeById(@RequestBody Long[] ids) {
        return R.ok(evaluationBenchmarksService.removeBatchByIds(CollUtil.toList(ids)));
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
    @HasPermission("agi_evaluationBenchmarks_export")
    public List<EvaluationBenchmarks> exportExcel(EvaluationBenchmarks req, Long[] ids) {
        return evaluationBenchmarksService.list(Wrappers.lambdaQuery(req).in(ArrayUtil.isNotEmpty(ids), EvaluationBenchmarks::getId, ids));
    }

    /**
     * 导入excel 表
     * @param evaluationBenchmarksList 对象实体列表
     * @param bindingResult 错误信息列表
     * @return ok fail
     */
	@Operation(summary = "导入", description = "导入")
    @PostMapping("/import")
    @HasPermission("agi_evaluationBenchmarks_export")
    public R importExcel(@RequestExcel List<EvaluationBenchmarks> evaluationBenchmarksList, BindingResult bindingResult) {
        return R.ok(evaluationBenchmarksService.saveBatch(evaluationBenchmarksList));
    }
}