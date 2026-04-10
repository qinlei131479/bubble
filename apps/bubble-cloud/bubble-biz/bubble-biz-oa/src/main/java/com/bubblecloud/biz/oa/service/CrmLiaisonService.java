package com.bubblecloud.biz.oa.service;

import java.util.List;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.entity.CustomerLiaison;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * 客户联系人（对齐 PHP {@code CustomerLiaisonController}）。
 *
 * @author qinlei
 * @date 2026/4/3 13:00
 */
public interface CrmLiaisonService extends UpService<CustomerLiaison> {

	List<CustomerLiaison> listByEid(int eid);

	ArrayNode createForm();

	Long store(int eid, JsonNode body, Long adminId);

	void update(Long id, JsonNode body);

	void softDelete(Long id);

	CustomerLiaison info(Long id);

	ArrayNode editForm(Long id);

}
