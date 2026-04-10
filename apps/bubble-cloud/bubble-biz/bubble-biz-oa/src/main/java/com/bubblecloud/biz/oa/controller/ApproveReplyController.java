package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.service.ApproveReplyService;
import com.bubblecloud.common.core.util.PojoConvertUtil;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.dto.ApproveReplySaveDTO;
import com.bubblecloud.oa.api.entity.ApproveReply;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 审批评价（对齐 PHP {@code ent/approve/reply}）。
 *
 * @author qinlei
 * @date 2026/4/2 15:00
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/approve/reply")
@Tag(name = "审批评价")
public class ApproveReplyController {

	private final ApproveReplyService approveReplyService;

	@PostMapping
	@Operation(summary = "保存审批评价")
	public R<String> create(@RequestBody ApproveReplySaveDTO dto) {
		ApproveReply obj = PojoConvertUtil.convertPojo(dto, ApproveReply.class);
		approveReplyService.create(obj);
		return R.phpOk(OaConstants.INSERT_SUCC);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除审批评价")
	public R<String> removeById(@PathVariable Long id) {
		approveReplyService.deleteById(id);
		return R.phpOk(OaConstants.DELETE_SUCC);
	}

}
