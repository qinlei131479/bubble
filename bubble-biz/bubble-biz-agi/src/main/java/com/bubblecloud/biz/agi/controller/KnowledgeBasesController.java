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
import com.bubblecloud.agi.api.entity.KnowledgeBases;
import com.bubblecloud.biz.agi.service.KnowledgeBasesService;

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
 * 知识库
 *
 * @author Rampart
 * @date 2026-02-13 09:30:12
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/knowledgeBases" )
@Tag(description = "knowledgeBases" , name = "知识库管理" )
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class KnowledgeBasesController {

    private final  KnowledgeBasesService knowledgeBasesService;

    /**
     * 分页查询
     * @param pg 分页对象
     * @param req 知识库
     * @return
     */
    @Operation(summary = "分页查询" , description = "分页查询" )
    @GetMapping("/page" )
    @HasPermission("agi_knowledgeBases_view")
    public R<Page<KnowledgeBases>> getKnowledgeBasesPage(@ParameterObject Pg pg, @ParameterObject KnowledgeBases req) {
       	pg.addOrderDefault(OrderItem.desc("t.id"));
		return R.ok(knowledgeBasesService.findPg(pg, req));
    }


    /**
     * 通过条件查询知识库
     * @param req 查询条件
     * @return R  对象列表
     */
    @Operation(summary = "通过条件查询" , description = "通过条件查询对象" )
    @GetMapping("/details" )
    @HasPermission("agi_knowledgeBases_view")
    public R<List<KnowledgeBases>> getDetails(@ParameterObject KnowledgeBases req) {
        return R.ok(knowledgeBasesService.list(Wrappers.query(req)));
    }

    /**
     * 新增知识库
     * @param req 知识库
     * @return R
     */
    @Operation(summary = "新增知识库" , description = "新增知识库" )
    @SysLog("新增知识库" )
    @PostMapping
    @HasPermission("agi_knowledgeBases_add")
    public R create(@RequestBody @Validated({Req.Create.class}) KnowledgeBases req) {
        return R.ok(knowledgeBasesService.create(req));
    }

    /**
     * 修改知识库
     * @param req 知识库
     * @return R
     */
    @Operation(summary = "修改知识库" , description = "修改知识库" )
    @SysLog("修改知识库" )
    @PutMapping
    @HasPermission("agi_knowledgeBases_edit")
    public R update(@RequestBody @Validated({Req.Update.class}) KnowledgeBases req) {
        return R.ok(knowledgeBasesService.update(req));
    }

    /**
     * 通过id删除知识库
     * @param ids id列表
     * @return R
     */
    @Operation(summary = "通过id删除知识库" , description = "通过id删除知识库" )
    @SysLog("通过id删除知识库" )
    @DeleteMapping
    @HasPermission("agi_knowledgeBases_del")
    public R removeById(@RequestBody Long[] ids) {
        return R.ok(knowledgeBasesService.removeBatchByIds(CollUtil.toList(ids)));
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
    @HasPermission("agi_knowledgeBases_export")
    public List<KnowledgeBases> exportExcel(KnowledgeBases req, Long[] ids) {
        return knowledgeBasesService.list(Wrappers.lambdaQuery(req).in(ArrayUtil.isNotEmpty(ids), KnowledgeBases::getId, ids));
    }

    /**
     * 导入excel 表
     * @param knowledgeBasesList 对象实体列表
     * @param bindingResult 错误信息列表
     * @return ok fail
     */
	@Operation(summary = "导入", description = "导入")
    @PostMapping("/import")
    @HasPermission("agi_knowledgeBases_export")
    public R importExcel(@RequestExcel List<KnowledgeBases> knowledgeBasesList, BindingResult bindingResult) {
        return R.ok(knowledgeBasesService.saveBatch(knowledgeBasesList));
    }
}