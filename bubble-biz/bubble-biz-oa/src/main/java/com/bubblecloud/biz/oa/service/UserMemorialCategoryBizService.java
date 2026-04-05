package com.bubblecloud.biz.oa.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.bubblecloud.oa.api.dto.memorial.MemorialCategorySaveDTO;
import com.bubblecloud.oa.api.vo.form.OaElFormVO;
import com.bubblecloud.oa.api.vo.memorial.MemorialCategoryIndexVO;

public interface UserMemorialCategoryBizService {

	MemorialCategoryIndexVO index(Long adminId);

	OaElFormVO createForm(Long adminId, long pid, ObjectMapper om);

	OaElFormVO editForm(Long adminId, long id, ObjectMapper om);

	Long store(Long adminId, MemorialCategorySaveDTO dto);

	void update(Long adminId, long id, MemorialCategorySaveDTO dto);

	void delete(Long adminId, long id);

}
