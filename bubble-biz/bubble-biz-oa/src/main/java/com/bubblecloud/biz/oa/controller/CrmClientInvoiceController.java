package com.bubblecloud.biz.oa.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.service.ClientInvoiceCrmService;
import com.bubblecloud.biz.oa.util.OaSecurityUtil;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.entity.ClientBill;
import com.bubblecloud.oa.api.entity.ClientInvoice;
import com.bubblecloud.oa.api.entity.ClientInvoiceLog;
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
 * 客户发票（对齐 PHP {@code ent/client/invoice}，含 {@code GET record/{id}}）。
 *
 * @author qinlei
 * @date 2026/4/3 16:00
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/client/invoice")
@Tag(name = "CRM发票")
public class CrmClientInvoiceController {

	private static int intFromBody(Map<String, Object> body, String key, int def) {
		if (body == null || !body.containsKey(key)) {
			return def;
		}
		Object o = body.get(key);
		if (o instanceof Number n) {
			return n.intValue();
		}
		try {
			return Integer.parseInt(String.valueOf(o));
		}
		catch (Exception e) {
			return def;
		}
	}

	private final ClientInvoiceCrmService clientInvoiceCrmService;

	private static int entid1(Integer v) {
		return ObjectUtil.defaultIfNull(v, 1);
	}

	private static Map<String, String> toMap(@RequestParam Map<String, String> p) {
		return p == null ? new HashMap<>() : new HashMap<>(p);
	}

	private static Integer parseInt(Map<String, String> m, String k) {
		if (m == null || !m.containsKey(k)) {
			return null;
		}
		try {
			String s = m.get(k);
			if (s == null || s.isBlank()) {
				return null;
			}
			return Integer.parseInt(s.trim());
		}
		catch (Exception e) {
			return null;
		}
	}

	private static int pageOf(Map<String, String> m) {
		Integer p = parseInt(m, "page");
		return p == null || p < 1 ? 1 : p;
	}

	private static int limitOf(Map<String, String> m) {
		Integer l = parseInt(m, "limit");
		return l == null || l < 1 ? 20 : l;
	}

	@GetMapping("/list")
	@Operation(summary = "财务发票列表")
	public R<Map<String, Object>> list(@RequestParam Map<String, String> params) {
		Map<String, String> q = toMap(params);
		return R.phpOk(clientInvoiceCrmService.listFinance(entid1(parseInt(q, "entid")), q, pageOf(q), limitOf(q)));
	}

	@GetMapping({"", "/"})
	@Operation(summary = "发票申请列表")
	public R<Map<String, Object>> index(@RequestParam Map<String, String> params) {
		Map<String, String> q = toMap(params);
		return R.phpOk(clientInvoiceCrmService.index(entid1(parseInt(q, "entid")), q, pageOf(q), limitOf(q)));
	}

	@PostMapping({"", "/"})
	@Operation(summary = "保存发票申请")
	public R<Map<String, Long>> store(@RequestBody JsonNode body, @RequestParam(defaultValue = "1") Integer entid) {
		Long uid = OaSecurityUtil.currentUserId();
		ClientInvoice row = clientInvoiceCrmService.store(entid1(entid), body, uid == null ? "" : String.valueOf(uid));
		return R.phpOk(Map.of("id", row.getId()));
	}

	@PutMapping("/{id}")
	@Operation(summary = "修改发票申请")
	public R<String> update(@PathVariable long id, @RequestBody JsonNode body, @RequestParam(defaultValue = "1") Integer entid) {
		clientInvoiceCrmService.update(id, entid1(entid), body);
		return R.phpOk(OaConstants.UPDATE_SUCC);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除发票申请")
	public R<String> destroy(@PathVariable long id) {
		clientInvoiceCrmService.destroy(id);
		return R.phpOk(OaConstants.DELETE_SUCC);
	}

	@GetMapping("/bill/{id}")
	@Operation(summary = "关联付款单")
	public R<List<ClientBill>> billList(@PathVariable long id) {
		return R.phpOk(clientInvoiceCrmService.billList(id));
	}

	@PostMapping("/status/{id}")
	@Operation(summary = "审核发票")
	public R<String> postStatus(@PathVariable long id, @RequestBody JsonNode body, @RequestParam(defaultValue = "1") Integer entid) {
		int st = body.path("status").asInt(0);
		clientInvoiceCrmService.statusAudit(id, entid1(entid), body, st);
		return R.phpOk("common.operation.succ");
	}

	@PutMapping("/status/{id}")
	@Operation(summary = "在线开票回调")
	public R<Map<String, Boolean>> callback(@PathVariable long id, @RequestBody Map<String, String> body) {
		String num = body == null ? "" : body.getOrDefault("invoice_num", "");
		String serial = body == null ? "" : body.getOrDefault("invoice_serial_number", "");
		clientInvoiceCrmService.callback(id, num, serial);
		return R.phpOk(Map.of("res", true));
	}

	@PutMapping("/mark/{id}")
	@Operation(summary = "修改发票备注")
	public R<String> setMark(@PathVariable long id, @RequestBody Map<String, String> body) {
		String mark = body == null ? "" : body.getOrDefault("mark", "");
		clientInvoiceCrmService.setMark(id, mark);
		return R.phpOk("common.operation.succ");
	}

	@PostMapping("/shift")
	@Operation(summary = "发票转移")
	public R<String> shift(@RequestBody JsonNode body) {
		clientInvoiceCrmService.shift(body);
		return R.phpOk("common.operation.succ");
	}

	@GetMapping("/invalid_form/{id}")
	@Operation(summary = "作废表单")
	public R<List<Map<String, Object>>> invalidForm(@PathVariable long id) {
		return R.phpOk(clientInvoiceCrmService.invalidForm(id));
	}

	@PutMapping("/invalid_apply/{id}")
	@Operation(summary = "作废申请")
	public R<String> invalidApply(@PathVariable long id, @RequestBody Map<String, Object> body) {
		int invalid = intFromBody(body, "invalid", 1);
		String remark = body == null ? "" : String.valueOf(body.getOrDefault("remark", ""));
		if (invalid != -1 && invalid != 1) {
			return R.phpFailed("参数错误");
		}
		clientInvoiceCrmService.invalidApply(id, invalid, remark);
		return R.phpOk("common.operation.succ");
	}

	@PutMapping("/invalid_review/{id}")
	@Operation(summary = "作废审核")
	public R<String> invalidReview(@PathVariable long id, @RequestBody Map<String, Object> body) {
		int invalid = intFromBody(body, "invalid", 2);
		String remark = body == null ? "" : String.valueOf(body.getOrDefault("remark", ""));
		if (invalid != 2 && invalid != 3) {
			return R.phpFailed("参数错误");
		}
		clientInvoiceCrmService.invalidReview(id, invalid, remark);
		return R.phpOk("common.operation.succ");
	}

	@GetMapping("/price_statistics")
	@Operation(summary = "累计/审核中/付款金额")
	public R<Map<String, String>> priceStatistics(@RequestParam(required = false) Integer eid,
			@RequestParam(required = false) Integer cid, @RequestParam(defaultValue = "1") Integer entid) {
		if ((eid == null || eid <= 0) && (cid == null || cid <= 0)) {
			return R.phpFailed("common.empty.attrs");
		}
		return R.phpOk(clientInvoiceCrmService.priceStatistics(entid1(entid), eid, cid));
	}

	@PutMapping("/withdraw/{id}")
	@Operation(summary = "开票撤回")
	public R<String> withdraw(@PathVariable long id, @RequestBody Map<String, String> body) {
		String remark = body == null ? "" : body.getOrDefault("remark", "");
		clientInvoiceCrmService.withdraw(id, remark);
		return R.phpOk("common.operation.succ");
	}

	@GetMapping("/info/{id}")
	@Operation(summary = "发票详情")
	public R<ClientInvoice> info(@PathVariable long id, @RequestParam(defaultValue = "1") Integer entid) {
		try {
			return R.phpOk(clientInvoiceCrmService.info(id, entid1(entid)));
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(StrUtil.blankToDefault(e.getMessage(), "common.operation.fail"));
		}
	}

	@GetMapping("/uri/{id}")
	@Operation(summary = "在线开票 URI（占位）")
	public R<Map<String, Object>> uri(@PathVariable long id) {
		try {
			return R.phpOk(clientInvoiceCrmService.invoiceUri(id));
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(StrUtil.blankToDefault(e.getMessage(), "common.operation.fail"));
		}
	}

	@GetMapping("/record/{invoiceId}")
	@Operation(summary = "开票操作记录")
	public R<List<ClientInvoiceLog>> record(@PathVariable long invoiceId, @RequestParam(defaultValue = "1") Integer entid) {
		if (invoiceId <= 0) {
			return R.phpFailed("common.empty.attrs");
		}
		return R.phpOk(clientInvoiceCrmService.record(invoiceId, entid1(entid)));
	}

}
