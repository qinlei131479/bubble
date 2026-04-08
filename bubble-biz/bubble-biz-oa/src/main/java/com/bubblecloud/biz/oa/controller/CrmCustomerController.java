package com.bubblecloud.biz.oa.controller;

import java.util.Collections;
import java.util.List;

import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.service.CrmCustomerService;
import com.bubblecloud.biz.oa.util.OaSecurityUtil;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.vo.ListCountVO;
import com.bubblecloud.oa.api.entity.Customer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
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
 * 客户管理（对齐 PHP {@code ent/client/customer}）。
 *
 * @author qinlei
 * @date 2026/4/3 10:00
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/client/customer")
@Tag(name = "CRM客户")
public class CrmCustomerController {

	private final CrmCustomerService crmCustomerService;

	private Long requireAdminId() {
		Long id = OaSecurityUtil.currentUserId();
		if (ObjectUtil.isNull(id)) {
			throw new IllegalStateException("未登录");
		}
		return id;
	}

	@PostMapping("/list")
	@Operation(summary = "客户列表")
	public R<ListCountVO<Customer>> list(@RequestBody JsonNode body) {
		return R.phpOk(crmCustomerService.postCustomerList(body, requireAdminId()));
	}

	@GetMapping("/create")
	@Operation(summary = "客户新增表单")
	public R<ArrayNode> create() {
		return R.phpOk(crmCustomerService.createForm());
	}

	@PostMapping({ "", "/" })
	@Operation(summary = "新增客户")
	public R<ObjectNode> store(@RequestBody JsonNode body) {
		ObjectNode data = crmCustomerService.storeFromBody(body, requireAdminId());
		return R.phpOk(data);
	}

	@GetMapping("/{id}/edit")
	@Operation(summary = "客户修改表单")
	public R<ArrayNode> edit(@PathVariable Long id) {
		return R.phpOk(crmCustomerService.editForm(id, requireAdminId()));
	}

	@PutMapping("/{id}")
	@Operation(summary = "修改客户")
	public R<String> update(@PathVariable Long id, @RequestBody JsonNode body,
			@RequestParam(defaultValue = "0") Integer force) {
		crmCustomerService.updateFromBody(id, body, requireAdminId(), force);
		return R.phpOk(OaConstants.UPDATE_SUCC);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除客户")
	public R<String> destroy(@PathVariable Long id) {
		crmCustomerService.softDelete(id);
		return R.phpOk(OaConstants.DELETE_SUCC);
	}

	@GetMapping("/info/{id}")
	@Operation(summary = "客户详情")
	public R<ObjectNode> info(@PathVariable Long id) {
		return R.phpOk(crmCustomerService.customerInfo(id, requireAdminId()));
	}

	@GetMapping("/list_statistics")
	@Operation(summary = "客户列表统计")
	public R<ObjectNode> listStatistics(@RequestParam(defaultValue = "1") Integer types,
			@RequestParam(required = false) List<Integer> uid) {
		return R.phpOk(crmCustomerService.listStatistics(types, requireAdminId(), uid));
	}

	@GetMapping("/select")
	@Operation(summary = "客户下拉")
	public R<ArrayNode> select() {
		return R.phpOk(crmCustomerService.selectCustomers(requireAdminId()));
	}

	@PostMapping("/lost")
	@Operation(summary = "客户流失")
	public R<String> lost(@RequestBody JsonNode body) {
		crmCustomerService.lost(ids(body), requireAdminId());
		return R.phpOk(OaConstants.OPT_SUCC);
	}

	@PostMapping("/return")
	@Operation(summary = "客户退回公海")
	public R<String> returnHigh(@RequestBody JsonNode body) {
		String reason = body.path("reason").asText("");
		crmCustomerService.returnToHighSeas(ids(body), reason, requireAdminId());
		return R.phpOk(OaConstants.OPT_SUCC);
	}

	@PostMapping("/subscribe/{id}/{status}")
	@Operation(summary = "修改关注状态")
	public R<String> subscribe(@PathVariable Long id, @PathVariable Integer status) {
		crmCustomerService.subscribe(id, status, requireAdminId());
		return R.phpOk(OaConstants.OPT_SUCC);
	}

	@PostMapping("/cancel_lost/{id}")
	@Operation(summary = "取消流失")
	public R<String> cancelLost(@PathVariable Long id) {
		crmCustomerService.cancelLost(id, requireAdminId());
		return R.phpOk(OaConstants.OPT_SUCC);
	}

	@GetMapping("/salesman")
	@Operation(summary = "业务员列表")
	public R<ArrayNode> salesman() {
		return R.phpOk(crmCustomerService.salesmanOptions());
	}

	@PostMapping("/claim")
	@Operation(summary = "公海领取")
	public R<String> claim(@RequestBody JsonNode body) {
		crmCustomerService.claim(ids(body), requireAdminId());
		return R.phpOk(OaConstants.OPT_SUCC);
	}

	@PostMapping("/label")
	@Operation(summary = "批量标签")
	public R<String> label(@RequestBody JsonNode body) {
		List<Long> data = ids(body);
		List<Integer> label = labelIds(body);
		crmCustomerService.batchLabels(data, label);
		return R.phpOk(OaConstants.OPT_SUCC);
	}

	@PostMapping("/shift")
	@Operation(summary = "客户转移")
	public R<String> shift(@RequestBody JsonNode body) {
		List<Long> data = ids(body);
		int toUid = body.path("to_uid").asInt(0);
		int invoice = body.path("invoice").asInt(0);
		int contract = body.path("contract").asInt(0);
		crmCustomerService.shift(data, toUid, invoice, contract, requireAdminId());
		return R.phpOk(OaConstants.OPT_SUCC);
	}

	@PostMapping("/statistics")
	@Operation(summary = "业绩统计")
	public R<ObjectNode> statistics(@RequestBody JsonNode body) {
		return R.phpOk(crmCustomerService.performanceStatistics(body, requireAdminId()));
	}

	@PostMapping("/contract_rank")
	@Operation(summary = "合同类型分析")
	public R<ArrayNode> contractRank(@RequestBody JsonNode body) {
		return R.phpOk(crmCustomerService.contractCategoryRank(body, requireAdminId()));
	}

	@PostMapping("/ranking")
	@Operation(summary = "业务员排行")
	public R<ObjectNode> ranking(@RequestBody JsonNode body) {
		return R.phpOk(crmCustomerService.salesmanRanking(body, requireAdminId()));
	}

	@PostMapping("/trend_statistics")
	@Operation(summary = "业绩趋势")
	public R<ObjectNode> trendStatistics(@RequestBody(required = false) JsonNode body) {
		JsonNode b = body == null ? JsonNodeFactory.instance.objectNode() : body;
		return R.phpOk(crmCustomerService.trendStatistics(b, requireAdminId()));
	}

	@PostMapping("/import")
	@Operation(summary = "客户导入")
	public R<String> importRows(@RequestBody JsonNode body) {
		crmCustomerService.batchImport(body, requireAdminId());
		return R.phpOk(OaConstants.OPT_SUCC);
	}

	private static List<Long> ids(JsonNode body) {
		JsonNode data = body.get("data");
		if (data == null || !data.isArray()) {
			return Collections.emptyList();
		}
		java.util.ArrayList<Long> out = new java.util.ArrayList<>();
		data.forEach(n -> out.add(n.asLong()));
		return out;
	}

	private static List<Integer> labelIds(JsonNode body) {
		JsonNode label = body.get("label");
		if (label == null || !label.isArray()) {
			return Collections.emptyList();
		}
		java.util.ArrayList<Integer> out = new java.util.ArrayList<>();
		label.forEach(n -> out.add(n.asInt()));
		return out;
	}

}
