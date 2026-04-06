package com.bubblecloud.biz.oa.service;

import java.util.List;

import com.bubblecloud.oa.api.dto.finance.BillListSaveDTO;
import com.bubblecloud.oa.api.entity.BillList;
import com.bubblecloud.oa.api.entity.ClientBillLog;
import com.bubblecloud.oa.api.vo.finance.BillListFinancePageVO;
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

	ObjectNode emptyChartShell();

	ObjectNode rankAnalysisStub();

	List<ClientBillLog> listLogs(Long entid, Long billListId);

	void importRows(JsonNode body, Long operatorUserId);

	BillList createBill(BillListSaveDTO dto, Long operatorUserId);

	void updateBill(Long id, BillListSaveDTO dto, Long operatorUserId);

	void deleteBill(Long id);

}
