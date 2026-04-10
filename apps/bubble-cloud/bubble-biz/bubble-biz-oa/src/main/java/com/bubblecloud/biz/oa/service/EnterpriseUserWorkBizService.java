package com.bubblecloud.biz.oa.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.bubblecloud.oa.api.dto.enterprise.EnterpriseUserWorkSaveDTO;
import com.bubblecloud.oa.api.entity.EnterpriseUserWork;
import com.bubblecloud.oa.api.vo.ListCountVO;
import com.bubblecloud.oa.api.vo.form.OaElFormVO;

/**
 * EnterpriseUserWorkBizService。
 *
 * @author qinlei
 * @date 2026/4/5
 */

public interface EnterpriseUserWorkBizService {

	ListCountVO<EnterpriseUserWork> list(Long userId);

	OaElFormVO createForm(Long userId, ObjectMapper om);

	OaElFormVO editForm(Long id, ObjectMapper om);

	Long save(EnterpriseUserWorkSaveDTO dto);

	void update(Long id, EnterpriseUserWorkSaveDTO dto);

	void remove(Long id);

}
