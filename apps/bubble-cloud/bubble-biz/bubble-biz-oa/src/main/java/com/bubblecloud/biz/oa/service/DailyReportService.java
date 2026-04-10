package com.bubblecloud.biz.oa.service;

import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.oa.api.dto.DailyReplyDTO;
import com.bubblecloud.oa.api.dto.DailySaveDTO;
import com.bubblecloud.oa.api.entity.EnterpriseUserDaily;
import com.bubblecloud.oa.api.vo.ListCountVO;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * 工作汇报 ent/daily（与 EnterpriseUserDaily 表）。
 *
 * @author qinlei
 * @date 2026/4/6 12:30
 */
public interface DailyReportService {

	ListCountVO<EnterpriseUserDaily> list(Pg<EnterpriseUserDaily> pg, Integer types, String time, Long filterUserId,
			Long entid);

	void save(Long operatorAdminId, String operatorUid, DailySaveDTO dto);

	void update(Long operatorAdminId, Long id, DailySaveDTO dto);

	JsonNode editDetail(Long id, Long entid);

	void deleteById(Long id);

	void replySave(String replierUid, DailyReplyDTO dto);

	void replyDelete(Long replyId, Long dailyId, String operatorUid);

	JsonNode reportMembers(Long adminId);

}
