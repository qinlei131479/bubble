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
import com.bubblecloud.agi.api.entity.Conversations;
import com.bubblecloud.biz.agi.service.ConversationsService;

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
 * 对话
 *
 * @author Rampart
 * @date 2026-02-13 09:36:42
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/conversations" )
@Tag(description = "conversations" , name = "对话管理" )
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class ConversationsController {

    private final  ConversationsService conversationsService;

    /**
     * 分页查询
     * @param pg 分页对象
     * @param req 对话
     * @return
     */
    @Operation(summary = "分页查询" , description = "分页查询" )
    @GetMapping("/page" )
    @HasPermission("agi_conversations_view")
    public R<Page<Conversations>> getConversationsPage(@ParameterObject Pg pg, @ParameterObject Conversations req) {
       	pg.addOrderDefault(OrderItem.desc("t.id"));
		return R.ok(conversationsService.findPg(pg, req));
    }


    /**
     * 通过条件查询对话
     * @param req 查询条件
     * @return R  对象列表
     */
    @Operation(summary = "通过条件查询" , description = "通过条件查询对象" )
    @GetMapping("/details" )
    @HasPermission("agi_conversations_view")
    public R<List<Conversations>> getDetails(@ParameterObject Conversations req) {
        return R.ok(conversationsService.list(Wrappers.query(req)));
    }

    /**
     * 新增对话
     * @param req 对话
     * @return R
     */
    @Operation(summary = "新增对话" , description = "新增对话" )
    @SysLog("新增对话" )
    @PostMapping
    @HasPermission("agi_conversations_add")
    public R create(@RequestBody @Validated({Req.Create.class}) Conversations req) {
        return R.ok(conversationsService.create(req));
    }

    /**
     * 修改对话
     * @param req 对话
     * @return R
     */
    @Operation(summary = "修改对话" , description = "修改对话" )
    @SysLog("修改对话" )
    @PutMapping
    @HasPermission("agi_conversations_edit")
    public R update(@RequestBody @Validated({Req.Update.class}) Conversations req) {
        return R.ok(conversationsService.update(req));
    }

    /**
     * 通过id删除对话
     * @param ids id列表
     * @return R
     */
    @Operation(summary = "通过id删除对话" , description = "通过id删除对话" )
    @SysLog("通过id删除对话" )
    @DeleteMapping
    @HasPermission("agi_conversations_del")
    public R removeById(@RequestBody Long[] ids) {
        return R.ok(conversationsService.removeBatchByIds(CollUtil.toList(ids)));
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
    @HasPermission("agi_conversations_export")
    public List<Conversations> exportExcel(Conversations req, Long[] ids) {
        return conversationsService.list(Wrappers.lambdaQuery(req).in(ArrayUtil.isNotEmpty(ids), Conversations::getId, ids));
    }

    /**
     * 导入excel 表
     * @param conversationsList 对象实体列表
     * @param bindingResult 错误信息列表
     * @return ok fail
     */
	@Operation(summary = "导入", description = "导入")
    @PostMapping("/import")
    @HasPermission("agi_conversations_export")
    public R importExcel(@RequestExcel List<Conversations> conversationsList, BindingResult bindingResult) {
        return R.ok(conversationsService.saveBatch(conversationsList));
    }
}