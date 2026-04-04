package com.bubblecloud.biz.oa.service;

import java.util.List;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.entity.Contract;
import com.bubblecloud.oa.api.vo.ListCountVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * CRM 合同（对齐 PHP {@code ContractController} / {@code ContractService}）。
 *
 * @author qinlei
 * @date 2026/4/3 12:00
 */
public interface CrmContractService extends UpService<Contract> {

	ListCountVO<Contract> postList(JsonNode body, Long adminId);

	ArrayNode createForm();

	void store(JsonNode body, Long adminId);

	void update(Long id, JsonNode body, Long adminId);

	void softDelete(Long id);

	ObjectNode info(Long id);

	ArrayNode editForm(Long id);

	ObjectNode listStatistics(int types, Long adminId);

	void subscribeContract(Long contractId, int status, Long adminId);

	ArrayNode selectByCustomerIds(List<Integer> eids, Long adminId);

	void abnormal(Long id, int status, Long adminId);

	void shift(JsonNode body);

	void batchImport(JsonNode body, Long adminId);

}
