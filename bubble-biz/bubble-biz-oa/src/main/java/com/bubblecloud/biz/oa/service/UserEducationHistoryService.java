package com.bubblecloud.biz.oa.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.bubblecloud.oa.api.dto.resume.UserEducationHistorySaveDTO;
import com.bubblecloud.oa.api.entity.UserEducationHistory;
import com.bubblecloud.oa.api.vo.ListCountVO;
import com.bubblecloud.oa.api.vo.form.OaElFormVO;

/**
 * UserEducationHistoryService。
 *
 * @author qinlei
 * @date 2026/4/5
 */

public interface UserEducationHistoryService {

	ListCountVO<UserEducationHistory> list(Long adminId, Long resumeIdFilter);

	OaElFormVO createForm(Long adminId, ObjectMapper om);

	OaElFormVO editForm(Long adminId, Long id, ObjectMapper om);

	Long save(Long adminId, UserEducationHistorySaveDTO dto);

	void update(Long adminId, Long id, UserEducationHistorySaveDTO dto);

	void remove(Long adminId, Long id);

}
