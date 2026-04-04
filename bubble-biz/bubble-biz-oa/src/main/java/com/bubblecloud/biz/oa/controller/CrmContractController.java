package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.service.CrmContractService;
import com.bubblecloud.biz.oa.util.OaSecurityUtil;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.entity.Contract;
import com.bubblecloud.oa.api.vo.ListCountVO;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
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
 * 合同管理（对齐 PHP {@code ent/client/contracts}）。
 *
 * @author qinlei
 * @date 2026/4/3 12:00
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/client/contracts")
@Tag(name = "CRM合同")
public class CrmContractController {

	private final CrmContractService crmContractService;

	private Long aid() {
		Long id = OaSecurityUtil.currentUserId();
		if (ObjectUtil.isNull(id)) {
			throw new IllegalStateException("未登录");
		}
		return id;
	}

	@PostMapping("/list")
	@Operation(summary = "合同列表")
	public R<ListCountVO<Contract>> list(@RequestBody JsonNode body) {
		return R.phpOk(crmContractService.postList(body, aid()));
	}

	@GetMapping("/create")
	@Operation(summary = "合同新增表单")
	public R<ArrayNode> create() {
		return R.phpOk(crmContractService.createForm());
	}

	@PostMapping({"", "/"})
	@Operation(summary = "新增合同")
	public R<String> store(@RequestBody JsonNode body) {
		crmContractService.store(body, aid());
		return R.phpOk(OaConstants.INSERT_SUCC);
	}

	@GetMapping("/{id}/edit")
	@Operation(summary = "合同修改表单")
	public R<ArrayNode> edit(@PathVariable Long id) {
		return R.phpOk(crmContractService.editForm(id));
	}

	@PutMapping("/{id}")
	@Operation(summary = "修改合同")
	public R<String> update(@PathVariable Long id, @RequestBody JsonNode body) {
		crmContractService.update(id, body, aid());
		return R.phpOk(OaConstants.UPDATE_SUCC);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除合同")
	public R<String> destroy(@PathVariable Long id) {
		crmContractService.softDelete(id);
		return R.phpOk(OaConstants.DELETE_SUCC);
	}

	@GetMapping("/info/{id}")
	@Operation(summary = "合同详情")
	public R<ObjectNode> info(@PathVariable Long id) {
		return R.phpOk(crmContractService.info(id));
	}

	@GetMapping("/list_statistics")
	@Operation(summary = "合同列表统计")
	public R<ObjectNode> listStatistics(@RequestParam(defaultValue = "5") Integer types) {
		return R.phpOk(crmContractService.listStatistics(types, aid()));
	}

	@PostMapping("/subscribe/{id}/{status}")
	@Operation(summary = "合同关注")
	public R<String> subscribe(@PathVariable Long id, @PathVariable Integer status) {
		crmContractService.subscribeContract(id, status, aid());
		return R.phpOk(OaConstants.OPT_SUCC);
	}

	@GetMapping("/select")
	@Operation(summary = "合同下拉")
	public R<ArrayNode> select(@RequestParam(required = false) List<Integer> data) {
		return R.phpOk(crmContractService.selectByCustomerIds(data, aid()));
	}

	@PutMapping("/abnormal/{id}/{status}")
	@Operation(summary = "合同异常状态")
	public R<String> abnormal(@PathVariable Long id, @PathVariable Integer status) {
		crmContractService.abnormal(id, status, aid());
		return R.phpOk(OaConstants.OPT_SUCC);
	}

	@PostMapping("/shift")
	@Operation(summary = "合同转移")
	public R<String> shift(@RequestBody JsonNode body) {
		crmContractService.shift(body);
		return R.phpOk(OaConstants.OPT_SUCC);
	}

	@PostMapping("/import")
	@Operation(summary = "合同导入")
	public R<String> importRows(@RequestBody JsonNode body) {
		crmContractService.batchImport(body, aid());
		return R.phpOk(OaConstants.OPT_SUCC);
	}

}
