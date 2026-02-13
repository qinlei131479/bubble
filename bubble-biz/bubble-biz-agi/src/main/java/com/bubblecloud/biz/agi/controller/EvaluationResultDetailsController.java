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
import com.bubblecloud.agi.api.entity.EvaluationResultDetails;
import com.bubblecloud.biz.agi.service.EvaluationResultDetailsService;

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
 * 评估结果明细
 *
 * @author Rampart
 * @date 2026-02-13 16:52:13
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/evaluationResultDetails" )
@Tag(description = "evaluationResultDetails" , name = "评估结果明细管理" )
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class EvaluationResultDetailsController {

    private final  EvaluationResultDetailsService evaluationResultDetailsService;

    /**
     * 分页查询
     * @param pg 分页对象
     * @param req 评估结果明细
     * @return
     */
    @Operation(summary = "分页查询" , description = "分页查询" )
    @GetMapping("/page" )
    @HasPermission("agi_evaluationResultDetails_view")
    public R<Page<EvaluationResultDetails>> getEvaluationResultDetailsPage(@ParameterObject Pg pg, @ParameterObject EvaluationResultDetails req) {
       	pg.addOrderDefault(OrderItem.desc("t.id"));
		return R.ok(evaluationResultDetailsService.findPg(pg, req));
    }


    /**
     * 通过条件查询评估结果明细
     * @param req 查询条件
     * @return R  对象列表
     */
    @Operation(summary = "通过条件查询" , description = "通过条件查询对象" )
    @GetMapping("/details" )
    @HasPermission("agi_evaluationResultDetails_view")
    public R<List<EvaluationResultDetails>> getDetails(@ParameterObject EvaluationResultDetails req) {
        return R.ok(evaluationResultDetailsService.list(Wrappers.query(req)));
    }

    /**
     * 新增评估结果明细
     * @param req 评估结果明细
     * @return R
     */
    @Operation(summary = "新增评估结果明细" , description = "新增评估结果明细" )
    @SysLog("新增评估结果明细" )
    @PostMapping
    @HasPermission("agi_evaluationResultDetails_add")
    public R create(@RequestBody @Validated({Req.Create.class}) EvaluationResultDetails req) {
        return R.ok(evaluationResultDetailsService.create(req));
    }

    /**
     * 修改评估结果明细
     * @param req 评估结果明细
     * @return R
     */
    @Operation(summary = "修改评估结果明细" , description = "修改评估结果明细" )
    @SysLog("修改评估结果明细" )
    @PutMapping
    @HasPermission("agi_evaluationResultDetails_edit")
    public R update(@RequestBody @Validated({Req.Update.class}) EvaluationResultDetails req) {
        return R.ok(evaluationResultDetailsService.update(req));
    }

    /**
     * 通过id删除评估结果明细
     * @param ids id列表
     * @return R
     */
    @Operation(summary = "通过id删除评估结果明细" , description = "通过id删除评估结果明细" )
    @SysLog("通过id删除评估结果明细" )
    @DeleteMapping
    @HasPermission("agi_evaluationResultDetails_del")
    public R removeById(@RequestBody Long[] ids) {
        return R.ok(evaluationResultDetailsService.removeBatchByIds(CollUtil.toList(ids)));
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
    @HasPermission("agi_evaluationResultDetails_export")
    public List<EvaluationResultDetails> exportExcel(EvaluationResultDetails req, Long[] ids) {
        return evaluationResultDetailsService.list(Wrappers.lambdaQuery(req).in(ArrayUtil.isNotEmpty(ids), EvaluationResultDetails::getId, ids));
    }

    /**
     * 导入excel 表
     * @param evaluationResultDetailsList 对象实体列表
     * @param bindingResult 错误信息列表
     * @return ok fail
     */
	@Operation(summary = "导入", description = "导入")
    @PostMapping("/import")
    @HasPermission("agi_evaluationResultDetails_export")
    public R importExcel(@RequestExcel List<EvaluationResultDetails> evaluationResultDetailsList, BindingResult bindingResult) {
        return R.ok(evaluationResultDetailsService.saveBatch(evaluationResultDetailsList));
    }
}