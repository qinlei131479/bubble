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
import com.bubblecloud.agi.api.entity.KnowledgeFiles;
import com.bubblecloud.biz.agi.service.KnowledgeFilesService;

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
 * 知识库文件
 *
 * @author Rampart
 * @date 2026-02-13 09:33:20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/knowledgeFiles" )
@Tag(description = "knowledgeFiles" , name = "知识库文件管理" )
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class KnowledgeFilesController {

    private final  KnowledgeFilesService knowledgeFilesService;

    /**
     * 分页查询
     * @param pg 分页对象
     * @param req 知识库文件
     * @return
     */
    @Operation(summary = "分页查询" , description = "分页查询" )
    @GetMapping("/page" )
    @HasPermission("agi_knowledgeFiles_view")
    public R<Page<KnowledgeFiles>> getKnowledgeFilesPage(@ParameterObject Pg pg, @ParameterObject KnowledgeFiles req) {
       	pg.addOrderDefault(OrderItem.desc("t.id"));
		return R.ok(knowledgeFilesService.findPg(pg, req));
    }


    /**
     * 通过条件查询知识库文件
     * @param req 查询条件
     * @return R  对象列表
     */
    @Operation(summary = "通过条件查询" , description = "通过条件查询对象" )
    @GetMapping("/details" )
    @HasPermission("agi_knowledgeFiles_view")
    public R<List<KnowledgeFiles>> getDetails(@ParameterObject KnowledgeFiles req) {
        return R.ok(knowledgeFilesService.list(Wrappers.query(req)));
    }

    /**
     * 新增知识库文件
     * @param req 知识库文件
     * @return R
     */
    @Operation(summary = "新增知识库文件" , description = "新增知识库文件" )
    @SysLog("新增知识库文件" )
    @PostMapping
    @HasPermission("agi_knowledgeFiles_add")
    public R create(@RequestBody @Validated({Req.Create.class}) KnowledgeFiles req) {
        return R.ok(knowledgeFilesService.create(req));
    }

    /**
     * 修改知识库文件
     * @param req 知识库文件
     * @return R
     */
    @Operation(summary = "修改知识库文件" , description = "修改知识库文件" )
    @SysLog("修改知识库文件" )
    @PutMapping
    @HasPermission("agi_knowledgeFiles_edit")
    public R update(@RequestBody @Validated({Req.Update.class}) KnowledgeFiles req) {
        return R.ok(knowledgeFilesService.update(req));
    }

    /**
     * 通过id删除知识库文件
     * @param ids id列表
     * @return R
     */
    @Operation(summary = "通过id删除知识库文件" , description = "通过id删除知识库文件" )
    @SysLog("通过id删除知识库文件" )
    @DeleteMapping
    @HasPermission("agi_knowledgeFiles_del")
    public R removeById(@RequestBody Long[] ids) {
        return R.ok(knowledgeFilesService.removeBatchByIds(CollUtil.toList(ids)));
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
    @HasPermission("agi_knowledgeFiles_export")
    public List<KnowledgeFiles> exportExcel(KnowledgeFiles req, Long[] ids) {
        return knowledgeFilesService.list(Wrappers.lambdaQuery(req).in(ArrayUtil.isNotEmpty(ids), KnowledgeFiles::getId, ids));
    }

    /**
     * 导入excel 表
     * @param knowledgeFilesList 对象实体列表
     * @param bindingResult 错误信息列表
     * @return ok fail
     */
	@Operation(summary = "导入", description = "导入")
    @PostMapping("/import")
    @HasPermission("agi_knowledgeFiles_export")
    public R importExcel(@RequestExcel List<KnowledgeFiles> knowledgeFilesList, BindingResult bindingResult) {
        return R.ok(knowledgeFilesService.saveBatch(knowledgeFilesList));
    }
}