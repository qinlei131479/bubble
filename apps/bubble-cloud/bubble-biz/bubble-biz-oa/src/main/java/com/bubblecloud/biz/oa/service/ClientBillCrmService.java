package com.bubblecloud.biz.oa.service;

import java.util.List;
import java.util.Map;

import com.bubblecloud.oa.api.entity.ClientBill;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * 客户付款/续费（对齐 PHP {@code ClientBillService} 主流程，不含财务总账 Task）。
 *
 * @author qinlei
 * @date 2026/4/3 16:00
 */
public interface ClientBillCrmService {

	Map<String, Object> index(int entid, Map<String, String> query, int page, int limit);

	Map<String, Object> billList(int entid, Map<String, String> query, int page, int limit);

	ClientBill store(int entid, ClientBill body, String uidStr);

	void updateBill(long id, int entid, ClientBill body, String uidStr, boolean finance);

	void destroy(long id, int entid);

	void statusUpdate(long id, int entid, JsonNode body, String uidStr);

	void setMark(long id, String mark);

	void withdraw(long id, int entid);

	Map<String, String> priceStatistics(int entid, int eid);

	List<ClientBill> unInvoicedList(int entid, int eid, Integer invoiceId, int page, int limit);

	void financeUpdate(long id, int entid, ClientBill body, String uidStr);

	void financeDelete(long id, int entid);

	ClientBill getInfo(long id, int entid);

	Map<String, String> contractStatistics(int cid, int entid);

	Map<String, String> customerStatistics(int eid, int entid);

	/**
	 * 合同续费到期摘要（按 {@code cate_id} 聚合），与 PHP 路由 {@code renew_census/{cid}} 及列表中的
	 * {@code renew_census} 字段对齐。
	 */
	Object getRenewCensus(int cid, int entid);

}
