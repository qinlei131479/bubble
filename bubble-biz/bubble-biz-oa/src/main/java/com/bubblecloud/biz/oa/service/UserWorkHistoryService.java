package com.bubblecloud.biz.oa.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.bubblecloud.oa.api.dto.resume.UserWorkHistorySaveDTO;
import com.bubblecloud.oa.api.entity.UserWorkHistory;
import com.bubblecloud.oa.api.vo.ListCountVO;
import com.bubblecloud.oa.api.vo.form.OaElFormVO;

public interface UserWorkHistoryService {

	ListCountVO<UserWorkHistory> list(Long adminId, Long resumeIdFilter);

	OaElFormVO createForm(Long adminId, ObjectMapper om);

	OaElFormVO editForm(Long adminId, Long id, ObjectMapper om);

	Long save(Long adminId, UserWorkHistorySaveDTO dto);

	void update(Long adminId, Long id, UserWorkHistorySaveDTO dto);

	void remove(Long adminId, Long id);

}
