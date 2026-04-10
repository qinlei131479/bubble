package com.bubblecloud.biz.oa.controller;

import java.util.List;

import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.service.BillListFinanceService;
import com.bubblecloud.biz.oa.util.OaSecurityUtil;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.dto.finance.BillListSaveDTO;
import com.bubblecloud.oa.api.entity.ClientBillLog;
import com.bubblecloud.oa.api.vo.finance.BillListFinancePageVO;
import com.bubblecloud.oa.api.vo.finance.FinanceBillRankRowVO;
import com.bubblecloud.oa.api.vo.form.OaElFormVO;
import com.fasterxml.jackson.databind.JsonNode;
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
 * 企业财务流水（PHP {@code ent/bill}，W-23）。
 *
 * @author qinlei
 * @date 2026/4/6 16:00
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/bill")
@Tag(name = "企业财务流水")
public class BillController {

	private final BillListFinanceService billListFinanceService;

	@PostMapping("/list")
	@Operation(summary = "财务流水列表")
	public R<BillListFinancePageVO> postList(@RequestBody JsonNode body) {
		try {
			return R.phpOk(billListFinanceService.postList(body));
		}
		catch (Exception e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@PostMapping("/chart")
	@Operation(summary = "财务流水统计图（对齐 PHP BillService::getTrend all=true）")
	public R<ObjectNode> chart(@RequestBody JsonNode body) {
		try {
			return R.phpOk(billListFinanceService.chart(body));
		}
		catch (Exception e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@PostMapping("/rank_analysis")
	@Operation(summary = "占比分析（对齐 PHP getRankAnalysis）")
	public R<List<FinanceBillRankRowVO>> rankAnalysis(@RequestBody JsonNode body) {
		try {
			return R.phpOk(billListFinanceService.rankAnalysis(body));
		}
		catch (Exception e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@GetMapping("/record/{id}")
	@Operation(summary = "财务流水操作记录")
	public R<List<ClientBillLog>> logs(@PathVariable Long id, @RequestParam(defaultValue = "1") Long entid) {
		return R.phpOk(billListFinanceService.listLogs(entid, id));
	}

	@PostMapping("/chart_part")
	@Operation(summary = "财务流水统计数据（对齐 PHP chart_part / getTrend all=false）")
	public R<ObjectNode> chartPart(@RequestBody JsonNode body) {
		try {
			return R.phpOk(billListFinanceService.chartPart(body));
		}
		catch (Exception e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@PostMapping("/import")
	@Operation(summary = "导入资金记录")
	public R<String> importBill(@RequestBody JsonNode body) {
		try {
			Long uid = OaSecurityUtil.currentUserId();
			billListFinanceService.importRows(body, ObjectUtil.defaultIfNull(uid, 0L));
			return R.phpOk("导入成功");
		}
		catch (Exception e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@GetMapping("/create")
	@Operation(summary = "创建表单（elForm 对齐）")
	public R<OaElFormVO> createForm(@RequestParam(defaultValue = "1") long entid) {
		return R.phpOk(billListFinanceService.buildBillCreateForm(entid));
	}

	@PostMapping
	@Operation(summary = "保存财务流水")
	public R<String> store(@RequestBody BillListSaveDTO dto) {
		try {
			Long uid = OaSecurityUtil.currentUserId();
			billListFinanceService.createBill(dto, ObjectUtil.defaultIfNull(uid, 0L));
			return R.phpOk(OaConstants.INSERT_SUCC);
		}
		catch (Exception e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@GetMapping("/{id}/edit")
	@Operation(summary = "编辑表单")
	public R<OaElFormVO> editForm(@PathVariable Long id, @RequestParam(defaultValue = "1") long entid) {
		return R.phpOk(billListFinanceService.buildBillEditForm(id, entid));
	}

	@PutMapping("/{id}")
	@Operation(summary = "修改财务流水")
	public R<String> update(@PathVariable Long id, @RequestBody BillListSaveDTO dto) {
		try {
			Long uid = OaSecurityUtil.currentUserId();
			billListFinanceService.updateBill(id, dto, ObjectUtil.defaultIfNull(uid, 0L));
			return R.phpOk(OaConstants.UPDATE_SUCC);
		}
		catch (Exception e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除财务流水")
	public R<String> destroy(@PathVariable Long id) {
		try {
			billListFinanceService.deleteBill(id);
			return R.phpOk(OaConstants.DELETE_SUCC);
		}
		catch (Exception e) {
			return R.phpFailed(e.getMessage());
		}
	}

}
