package com.bubblecloud.biz.oa.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.service.ClientBillCrmService;
import com.bubblecloud.biz.oa.util.OaSecurityUtil;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.entity.ClientBill;
import com.fasterxml.jackson.databind.JsonNode;
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
import cn.hutool.core.util.StrUtil;

/**
 * 客户付款记录（对齐 PHP {@code ent/client/bill}）。
 *
 * @author qinlei
 * @date 2026/4/3 16:00
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/client/bill")
@Tag(name = "CRM付款记录")
public class CrmClientBillController {

	private final ClientBillCrmService clientBillCrmService;

	private static int entid1(Integer v) {
		return ObjectUtil.defaultIfNull(v, 1);
	}

	private static Map<String, String> toStrMap(@RequestParam Map<String, String> params) {
		return params == null ? new HashMap<>() : new HashMap<>(params);
	}

	private static int pageOf(Map<String, String> m) {
		Integer p = parseInt(m, "page");
		return p == null || p < 1 ? 1 : p;
	}

	private static int limitOf(Map<String, String> m) {
		Integer l = parseInt(m, "limit");
		return l == null || l < 1 ? 20 : l;
	}

	private static Integer parseInt(Map<String, String> m, String k) {
		if (m == null || !m.containsKey(k)) {
			return null;
		}
		try {
			return Integer.parseInt(m.get(k).trim());
		}
		catch (Exception e) {
			return null;
		}
	}

	@GetMapping({"", "/"})
	@Operation(summary = "付款记录列表（含 census）")
	public R<Map<String, Object>> index(@RequestParam Map<String, String> params) {
		Map<String, String> q = toStrMap(params);
		return R.phpOk(clientBillCrmService.index(entid1(parseInt(q, "entid")), q, pageOf(q), limitOf(q)));
	}

	@GetMapping("/list")
	@Operation(summary = "付款记录列表（eid 转合同维度）")
	public R<Map<String, Object>> list(@RequestParam Map<String, String> params) {
		Map<String, String> q = toStrMap(params);
		return R.phpOk(clientBillCrmService.billList(entid1(parseInt(q, "entid")), q, pageOf(q), limitOf(q)));
	}

	@PostMapping({"", "/"})
	@Operation(summary = "新增付款记录")
	public R<Map<String, Long>> store(@RequestBody ClientBill body, @RequestParam(defaultValue = "1") Integer entid) {
		Long uid = OaSecurityUtil.currentUserId();
		String u = uid == null ? "" : String.valueOf(uid);
		ClientBill row = clientBillCrmService.store(entid1(entid), body, u);
		return R.phpOk(Map.of("id", row.getId()));
	}

	@PutMapping("/{id}")
	@Operation(summary = "修改付款记录")
	public R<String> update(@PathVariable long id, @RequestBody ClientBill body, @RequestParam(defaultValue = "1") Integer entid) {
		Long uid = OaSecurityUtil.currentUserId();
		clientBillCrmService.updateBill(id, entid1(entid), body, uid == null ? "" : String.valueOf(uid), false);
		return R.phpOk(OaConstants.UPDATE_SUCC);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除付款记录")
	public R<String> destroy(@PathVariable long id, @RequestParam(defaultValue = "1") Integer entid) {
		clientBillCrmService.destroy(id, entid1(entid));
		return R.phpOk(OaConstants.DELETE_SUCC);
	}

	@PostMapping("/status/{id}")
	@Operation(summary = "财务审核付款记录")
	public R<String> setStatus(@PathVariable long id, @RequestBody JsonNode body, @RequestParam(defaultValue = "1") Integer entid) {
		Long uid = OaSecurityUtil.currentUserId();
		clientBillCrmService.statusUpdate(id, entid1(entid), body, uid == null ? "" : String.valueOf(uid));
		return R.phpOk("common.operation.succ");
	}

	@PutMapping("/mark/{id}")
	@Operation(summary = "修改付款备注")
	public R<String> setMark(@PathVariable long id, @RequestBody Map<String, String> body) {
		String mark = body == null ? "" : body.getOrDefault("mark", "");
		clientBillCrmService.setMark(id, mark);
		return R.phpOk("common.operation.succ");
	}

	@PutMapping("/withdraw/{id}")
	@Operation(summary = "撤回付款申请")
	public R<String> withdraw(@PathVariable long id, @RequestParam(defaultValue = "1") Integer entid) {
		clientBillCrmService.withdraw(id, entid1(entid));
		return R.phpOk("common.operation.succ");
	}

	@GetMapping("/price_statistics/{eid}")
	@Operation(summary = "累计付款/审核中金额")
	public R<Map<String, String>> priceStatistics(@PathVariable int eid, @RequestParam(defaultValue = "1") Integer entid) {
		if (eid <= 0) {
			return R.phpFailed("common.empty.attrs");
		}
		return R.phpOk(clientBillCrmService.priceStatistics(entid1(entid), eid));
	}

	@GetMapping("/un_invoiced_list")
	@Operation(summary = "待开票付款列表")
	public R<List<ClientBill>> unInvoicedList(@RequestParam Map<String, String> params) {
		Map<String, String> q = toStrMap(params);
		Integer eid = parseInt(q, "eid");
		if (eid == null || eid <= 0) {
			return R.phpFailed("common.empty.attrs");
		}
		Integer invoiceId = parseInt(q, "invoice_id");
		List<ClientBill> list = clientBillCrmService.unInvoicedList(entid1(parseInt(q, "entid")), eid, invoiceId, pageOf(q),
			limitOf(q));
		return R.phpOk(list);
	}

	@PutMapping("/finance/{id}")
	@Operation(summary = "财务编辑付款记录")
	public R<String> financeUpdate(@PathVariable long id, @RequestBody ClientBill body, @RequestParam(defaultValue = "1") Integer entid) {
		Long uid = OaSecurityUtil.currentUserId();
		clientBillCrmService.financeUpdate(id, entid1(entid), body, uid == null ? "" : String.valueOf(uid));
		return R.phpOk(OaConstants.UPDATE_SUCC);
	}

	@DeleteMapping("/finance/{id}")
	@Operation(summary = "财务删除付款记录")
	public R<String> financeDelete(@PathVariable long id, @RequestParam(defaultValue = "1") Integer entid) {
		clientBillCrmService.financeDelete(id, entid1(entid));
		return R.phpOk("common.operation.succ");
	}

	@GetMapping("/contract_statistics/{cid}")
	@Operation(summary = "合同统计")
	public R<Map<String, String>> contractStatistics(@PathVariable int cid, @RequestParam(defaultValue = "1") Integer entid) {
		if (cid <= 0) {
			return R.phpFailed("common.empty.attrs");
		}
		return R.phpOk(clientBillCrmService.contractStatistics(cid, entid1(entid)));
	}

	@GetMapping("/customer_statistics/{eid}")
	@Operation(summary = "客户统计")
	public R<Map<String, String>> customerStatistics(@PathVariable int eid, @RequestParam(defaultValue = "1") Integer entid) {
		if (eid <= 0) {
			return R.phpFailed("common.empty.attrs");
		}
		return R.phpOk(clientBillCrmService.customerStatistics(eid, entid1(entid)));
	}

	@GetMapping("/renew_census/{cid}")
	@Operation(summary = "续费到期统计")
	public R<Object> renewCensus(@PathVariable int cid, @RequestParam(defaultValue = "1") Integer entid) {
		if (cid <= 0) {
			return R.phpFailed("common.empty.attrs");
		}
		return R.phpOk(clientBillCrmService.getRenewCensus(cid, entid1(entid)));
	}

	@GetMapping("/{id}")
	@Operation(summary = "付款记录详情")
	public R<ClientBill> info(@PathVariable long id, @RequestParam(defaultValue = "1") Integer entid) {
		if (id <= 0) {
			return R.phpFailed("common.empty.attrs");
		}
		try {
			return R.phpOk(clientBillCrmService.getInfo(id, entid1(entid)));
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(StrUtil.blankToDefault(e.getMessage(), "common.operation.fail"));
		}
	}

}
