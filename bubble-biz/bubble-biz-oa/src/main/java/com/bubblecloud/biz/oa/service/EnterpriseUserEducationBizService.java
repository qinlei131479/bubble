package com.bubblecloud.biz.oa.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.bubblecloud.oa.api.dto.enterprise.EnterpriseUserEducationSaveDTO;
import com.bubblecloud.oa.api.entity.EnterpriseUserEducation;
import com.bubblecloud.oa.api.vo.ListCountVO;
import com.bubblecloud.oa.api.vo.form.OaElFormVO;

public interface EnterpriseUserEducationBizService {

	ListCountVO<EnterpriseUserEducation> list(Long userId);

	OaElFormVO createForm(Long userId, ObjectMapper om);

	OaElFormVO editForm(Long id, ObjectMapper om);

	Long save(EnterpriseUserEducationSaveDTO dto);

	void update(Long id, EnterpriseUserEducationSaveDTO dto);

	void remove(Long id);

}
