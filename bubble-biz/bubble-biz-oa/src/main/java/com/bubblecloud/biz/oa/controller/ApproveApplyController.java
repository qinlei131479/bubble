package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.service.ApproveApplyService;
import com.bubblecloud.biz.oa.util.OaSecurityUtil;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.oa.api.entity.ApproveApply;
import com.bubblecloud.oa.api.vo.ListCountVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import cn.hutool.core.util.ObjectUtil;

/**
 * 审批申请（PHP ent/approve/apply）。
 *
 * @author qinlei
 * @date 2026/4/6 12:00
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/approve/apply")
@Tag(name = "审批申请")
public class ApproveApplyController {

	private final ApproveApplyService approveApplyService;

	private final ObjectMapper objectMapper;

	@GetMapping({ "", "/" })
	@Operation(summary = "审批申请列表")
	public R<ListCountVO<ApproveApply>> index(@ParameterObject Pg pg, @RequestParam(defaultValue = "1") Long entid) {
		ApproveApply q = new ApproveApply();
		q.setFilterEntid(entid);
		Page<ApproveApply> res = approveApplyService.findPg(pg, q);
		return R.phpOk(ListCountVO.of(res.getRecords(), res.getTotal()));
	}

	@GetMapping("/{id}/edit")
	@Operation(summary = "审批申请详情")
	public R<ApproveApply> edit(@PathVariable Long id) {
		return R.phpOk(approveApplyService.getById(id));
	}

	@PutMapping("/{id}")
	@Operation(summary = "修改审批申请")
	public R<String> update(@PathVariable Long id, @RequestBody ApproveApply body) {
		if (ObjectUtil.isNull(body)) {
			return R.phpFailed("参数错误");
		}
		body.setId(id);
		approveApplyService.update(body);
		return R.phpOk(OaConstants.UPDATE_SUCC);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除审批申请")
	public R<String> destroy(@PathVariable Long id) {
		approveApplyService.removeById(id);
		return R.phpOk(OaConstants.DELETE_SUCC);
	}

	@GetMapping("/verify/{id}/{status}")
	@Operation(summary = "处理审批（占位更新状态）")
	public R<String> verify(@PathVariable Long id, @PathVariable Integer status) {
		try {
			approveApplyService.verifyApply(id, OaSecurityUtil.currentUserId(), status);
			return R.phpOk(OaConstants.OPT_SUCC);
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@GetMapping("/form/{id}")
	@Operation(summary = "获取审批申请表单（占位）")
	public R<JsonNode> applyForm(@PathVariable Long id) {
		ObjectNode n = objectMapper.createObjectNode();
		n.put("approve_id", id);
		return R.phpOk(n);
	}

	@PostMapping("/form/{id}")
	@Operation(summary = "获取审批人员列表（占位）")
	public R<JsonNode> verifyForm(@PathVariable Long id) {
		return R.phpOk(objectMapper.createArrayNode());
	}

	@PostMapping("/save/{id}")
	@Operation(summary = "保存审批申请（占位成功）")
	public R<String> save(@PathVariable Long id) {
		return R.phpOk(OaConstants.INSERT_SUCC);
	}

	@PostMapping("/revoke/{id}")
	@Operation(summary = "撤销申请")
	public R<String> revoke(@PathVariable Long id, @RequestBody(required = false) ObjectNode body) {
		try {
			String info = body != null && body.has("info") ? body.get("info").asText() : "";
			approveApplyService.revokeApply(id, OaSecurityUtil.currentUserId(), info);
			return R.phpOk(OaConstants.OPT_SUCC);
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@GetMapping("/export")
	@Operation(summary = "导出审批记录（占位空列表）")
	public R<JsonNode> export() {
		return R.phpOk(objectMapper.createArrayNode());
	}

	@GetMapping("/urge/{id}")
	@Operation(summary = "催办（占位）")
	public R<String> urge(@PathVariable Long id) {
		return R.phpOk("操作成功");
	}

	@PostMapping("/sign/{id}")
	@Operation(summary = "加签（占位）")
	public R<String> sign(@PathVariable Long id) {
		return R.phpOk("操作成功");
	}

	@PostMapping("/transfer/{id}")
	@Operation(summary = "转审（占位）")
	public R<String> transfer(@PathVariable Long id) {
		return R.phpOk("操作成功");
	}

}
