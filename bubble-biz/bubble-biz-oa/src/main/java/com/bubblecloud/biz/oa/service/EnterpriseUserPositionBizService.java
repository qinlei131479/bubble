package com.bubblecloud.biz.oa.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.bubblecloud.oa.api.dto.enterprise.EnterpriseUserPositionSaveDTO;
import com.bubblecloud.oa.api.entity.EnterpriseUserPosition;
import com.bubblecloud.oa.api.vo.ListCountVO;
import com.bubblecloud.oa.api.vo.form.OaElFormVO;

public interface EnterpriseUserPositionBizService {

	ListCountVO<EnterpriseUserPosition> list(Long userId);

	OaElFormVO createForm(Long userId, ObjectMapper om);

	OaElFormVO editForm(Long id, ObjectMapper om);

	Long save(EnterpriseUserPositionSaveDTO dto);

	void update(Long id, EnterpriseUserPositionSaveDTO dto);

	void remove(Long id);

}
