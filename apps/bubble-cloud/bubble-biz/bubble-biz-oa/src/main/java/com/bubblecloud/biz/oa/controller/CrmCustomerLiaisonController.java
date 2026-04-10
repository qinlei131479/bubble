package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.service.CrmLiaisonService;
import com.bubblecloud.biz.oa.util.OaSecurityUtil;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.entity.CustomerLiaison;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
 * 客户联系人（对齐 PHP {@code ent/client/liaisons}）。
 *
 * @author qinlei
 * @date 2026/4/3 13:00
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/client/liaisons")
@Tag(name = "CRM联系人")
public class CrmCustomerLiaisonController {

	private final CrmLiaisonService crmLiaisonService;

	private final ObjectMapper objectMapper;

	private Long aid() {
		Long id = OaSecurityUtil.currentUserId();
		if (ObjectUtil.isNull(id)) {
			throw new IllegalStateException("未登录");
		}
		return id;
	}

	@GetMapping({ "", "/" })
	@Operation(summary = "联系人列表")
	public R<java.util.List<CustomerLiaison>> index(@RequestParam Integer eid) {
		return R.phpOk(crmLiaisonService.listByEid(eid));
	}

	@GetMapping("/create")
	@Operation(summary = "新增表单")
	public R<ArrayNode> create() {
		return R.phpOk(crmLiaisonService.createForm());
	}

	@PostMapping({ "", "/" })
	@Operation(summary = "保存联系人")
	public R<ObjectNode> store(@RequestBody JsonNode body) {
		int eid = body.path("eid").asInt(0);
		if (eid < 1) {
			return R.phpFailed("客户数据异常");
		}
		Long id = crmLiaisonService.store(eid, body, aid());
		ObjectNode o = objectMapper.createObjectNode();
		o.put("id", id);
		return R.phpOk(o);
	}

	@GetMapping("/{id}/edit")
	@Operation(summary = "修改表单")
	public R<ArrayNode> edit(@PathVariable Long id) {
		return R.phpOk(crmLiaisonService.editForm(id));
	}

	@PutMapping("/{id}")
	@Operation(summary = "修改联系人")
	public R<String> update(@PathVariable Long id, @RequestBody JsonNode body) {
		crmLiaisonService.update(id, body);
		return R.phpOk(OaConstants.UPDATE_SUCC);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除联系人")
	public R<String> destroy(@PathVariable Long id) {
		crmLiaisonService.softDelete(id);
		return R.phpOk(OaConstants.DELETE_SUCC);
	}

	@GetMapping("/info/{id}")
	@Operation(summary = "联系人详情")
	public R<CustomerLiaison> info(@PathVariable Long id) {
		return R.phpOk(crmLiaisonService.info(id));
	}

}
