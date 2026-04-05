package com.bubblecloud.biz.oa.service;

import java.util.List;
import java.util.Map;

import com.bubblecloud.oa.api.vo.company.CompanyCardListDataVO;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * 员工档案 {@code ent/company/card}（对齐 PHP UserCardController + AdminService）。
 *
 * @author qinlei
 */
public interface CompanyCardService {

	CompanyCardListDataVO list(long entid, JsonNode body);

	JsonNode cardDetail(long entid, long id);

	void saveCard(long entid, int saveType, JsonNode body);

	void updateCard(long entid, long id, JsonNode body);

	void entry(long entid, long id);

	JsonNode formalForm(long entid, long id);

	void beFormal(long entid, long id, JsonNode body);

	void quit(long entid, long id, JsonNode body);

	void deleteCard(long entid, long id);

	void deleteBatch(long entid, List<Long> ids);

	void batchSetFrame(long entid, List<Integer> frameIds, List<Long> userIds, int mastartId);

	Map<String, Object> importTemplate();

	String importCards(long entid, JsonNode body);

	Map<String, Object> sendPerfect(long entid, long creatorAdminId, long cardId);

	Map<String, Object> sendInterview(long entid);

	List<Map<String, Object>> changeList(long entid, Long cardIdFilter);

}
