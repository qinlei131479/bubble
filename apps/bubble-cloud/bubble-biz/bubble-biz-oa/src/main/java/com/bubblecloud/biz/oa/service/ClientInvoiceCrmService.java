package com.bubblecloud.biz.oa.service;

import java.util.List;
import java.util.Map;

import com.bubblecloud.oa.api.entity.ClientBill;
import com.bubblecloud.oa.api.entity.ClientInvoice;
import com.bubblecloud.oa.api.entity.ClientInvoiceLog;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * 客户发票（对齐 PHP {@code ClientInvoiceService} 主接口；在线开票网关未对接时 URI 返回占位）。
 *
 * @author qinlei
 * @date 2026/4/3 16:00
 */
public interface ClientInvoiceCrmService {

	Map<String, Object> index(int entid, Map<String, String> query, int page, int limit);

	Map<String, Object> listFinance(int entid, Map<String, String> query, int page, int limit);

	ClientInvoice store(int entid, JsonNode body, String uidStr);

	void update(long id, int entid, JsonNode body);

	void destroy(long id);

	List<ClientBill> billList(long invoiceId);

	void statusAudit(long id, int entid, JsonNode body, int status);

	void setMark(long id, String mark);

	void shift(JsonNode body);

	List<Map<String, Object>> invalidForm(long id);

	void invalidApply(long id, int invalid, String remark);

	void invalidReview(long id, int invalid, String remark);

	Map<String, String> priceStatistics(int entid, Integer eid, Integer cid);

	void withdraw(long id, String remark);

	ClientInvoice info(long id, int entid);

	Map<String, Object> invoiceUri(long id);

	void callback(long id, String invoiceNum, String serialNumber);

	List<ClientInvoiceLog> record(long invoiceId, int entid);

}
