package com.bubblecloud.biz.oa.service;

import java.util.List;

import com.bubblecloud.oa.api.dto.finance.BillListSaveDTO;
import com.bubblecloud.oa.api.entity.BillList;
import com.bubblecloud.oa.api.entity.ClientBillLog;
import com.bubblecloud.oa.api.vo.finance.BillListFinancePageVO;
import com.bubblecloud.oa.api.vo.finance.FinanceBillRankRowVO;
import com.bubblecloud.oa.api.vo.form.OaElFormVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 企业财务流水（PHP ent/bill）业务。
 *
 * @author qinlei
 * @date 2026/4/6 16:00
 */
public interface BillListFinanceService {

	BillListFinancePageVO postList(JsonNode body);

	/**
	 * 对齐 PHP {@code BillController::billTrend} + {@code BillService::getTrend}（all=true）
	 */
	ObjectNode chart(JsonNode body);

	/** 对齐 PHP {@code BillController::billChart} + {@code getTrend}（all=false） */
	ObjectNode chartPart(JsonNode body);

	/** 对齐 PHP {@code getRankAnalysis} */
	List<FinanceBillRankRowVO> rankAnalysis(JsonNode body);

	List<ClientBillLog> listLogs(Long entid, Long billListId);

	void importRows(JsonNode body, Long operatorUserId);

	BillList createBill(BillListSaveDTO dto, Long operatorUserId);

	void updateBill(Long id, BillListSaveDTO dto, Long operatorUserId);

	void deleteBill(Long id);

	OaElFormVO buildBillCreateForm(long entid);

	OaElFormVO buildBillEditForm(long id, long entid);

}
