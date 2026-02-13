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
import com.bubblecloud.agi.api.entity.EvaluationResults;
import com.bubblecloud.biz.agi.service.EvaluationResultsService;

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
 * 评估结果
 *
 * @author Rampart
 * @date 2026-02-13 16:53:41
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/evaluationResults" )
@Tag(description = "evaluationResults" , name = "评估结果管理" )
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class EvaluationResultsController {

    private final  EvaluationResultsService evaluationResultsService;

    /**
     * 分页查询
     * @param pg 分页对象
     * @param req 评估结果
     * @return
     */
    @Operation(summary = "分页查询" , description = "分页查询" )
    @GetMapping("/page" )
    @HasPermission("agi_evaluationResults_view")
    public R<Page<EvaluationResults>> getEvaluationResultsPage(@ParameterObject Pg pg, @ParameterObject EvaluationResults req) {
       	pg.addOrderDefault(OrderItem.desc("t.id"));
		return R.ok(evaluationResultsService.findPg(pg, req));
    }


    /**
     * 通过条件查询评估结果
     * @param req 查询条件
     * @return R  对象列表
     */
    @Operation(summary = "通过条件查询" , description = "通过条件查询对象" )
    @GetMapping("/details" )
    @HasPermission("agi_evaluationResults_view")
    public R<List<EvaluationResults>> getDetails(@ParameterObject EvaluationResults req) {
        return R.ok(evaluationResultsService.list(Wrappers.query(req)));
    }

    /**
     * 新增评估结果
     * @param req 评估结果
     * @return R
     */
    @Operation(summary = "新增评估结果" , description = "新增评估结果" )
    @SysLog("新增评估结果" )
    @PostMapping
    @HasPermission("agi_evaluationResults_add")
    public R create(@RequestBody @Validated({Req.Create.class}) EvaluationResults req) {
        return R.ok(evaluationResultsService.create(req));
    }

    /**
     * 修改评估结果
     * @param req 评估结果
     * @return R
     */
    @Operation(summary = "修改评估结果" , description = "修改评估结果" )
    @SysLog("修改评估结果" )
    @PutMapping
    @HasPermission("agi_evaluationResults_edit")
    public R update(@RequestBody @Validated({Req.Update.class}) EvaluationResults req) {
        return R.ok(evaluationResultsService.update(req));
    }

    /**
     * 通过id删除评估结果
     * @param ids id列表
     * @return R
     */
    @Operation(summary = "通过id删除评估结果" , description = "通过id删除评估结果" )
    @SysLog("通过id删除评估结果" )
    @DeleteMapping
    @HasPermission("agi_evaluationResults_del")
    public R removeById(@RequestBody Long[] ids) {
        return R.ok(evaluationResultsService.removeBatchByIds(CollUtil.toList(ids)));
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
    @HasPermission("agi_evaluationResults_export")
    public List<EvaluationResults> exportExcel(EvaluationResults req, Long[] ids) {
        return evaluationResultsService.list(Wrappers.lambdaQuery(req).in(ArrayUtil.isNotEmpty(ids), EvaluationResults::getId, ids));
    }

    /**
     * 导入excel 表
     * @param evaluationResultsList 对象实体列表
     * @param bindingResult 错误信息列表
     * @return ok fail
     */
	@Operation(summary = "导入", description = "导入")
    @PostMapping("/import")
    @HasPermission("agi_evaluationResults_export")
    public R importExcel(@RequestExcel List<EvaluationResults> evaluationResultsList, BindingResult bindingResult) {
        return R.ok(evaluationResultsService.saveBatch(evaluationResultsList));
    }
}