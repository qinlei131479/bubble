package com.bubblecloud.biz.oa.service;

import java.util.List;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.entity.Customer;
import com.bubblecloud.oa.api.vo.ListCountVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * CRM 客户（对齐 PHP {@code CustomerController} / {@code CustomerService}）。
 *
 * @author qinlei
 * @date 2026/4/3 10:00
 */
public interface CrmCustomerService extends UpService<Customer> {

	ListCountVO<Customer> postCustomerList(JsonNode body, Long adminId);

	ObjectNode listStatistics(int types, Long adminId, List<Integer> uidScope);

	ArrayNode createForm();

	ObjectNode storeFromBody(JsonNode body, Long adminId);

	void updateFromBody(Long id, JsonNode body, Long adminId, int force);

	void softDelete(Long id);

	ObjectNode customerInfo(Long id, Long adminId);

	ArrayNode editForm(Long id, Long adminId);

	void lost(List<Long> ids, Long adminId);

	void returnToHighSeas(List<Long> ids, String reason, Long adminId);

	void subscribe(Long customerId, int status, Long adminId);

	void cancelLost(Long id, Long adminId);

	ArrayNode salesmanOptions();

	ArrayNode selectCustomers(Long adminId);

	void claim(List<Long> ids, Long adminId);

	void batchLabels(List<Long> customerIds, List<Integer> labelIds);

	void shift(List<Long> customerIds, int toUid, int invoice, int contract, Long adminId);

	ObjectNode performanceStatistics(JsonNode body, Long adminId);

	ArrayNode contractCategoryRank(JsonNode body, Long adminId);

	ObjectNode salesmanRanking(JsonNode body, Long adminId);

	ObjectNode trendStatistics(JsonNode body, Long adminId);

	void batchImport(JsonNode body, Long adminId);

}
