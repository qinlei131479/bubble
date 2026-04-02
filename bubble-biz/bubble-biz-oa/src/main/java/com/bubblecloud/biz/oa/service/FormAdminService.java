package com.bubblecloud.biz.oa.service;

import java.util.List;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.entity.FormData;
import com.bubblecloud.oa.api.vo.form.FormCateListItemVO;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * 自定义表单管理（PHP {@code FormController} / {@code FormService}）。
 *
 * @author qinlei
 * @date 2026/3/30
 */
public interface FormAdminService extends UpService<FormData> {

	List<FormCateListItemVO> listByTypes(Integer types);

	Long saveCate(Integer types, String title, Integer sort, Integer status);

	void updateCate(Long id, String title, Integer sort, Integer status);

	void deleteCate(Long id);

	void updateCateStatus(Long id, Integer status);

	void saveFormData(Integer types, JsonNode body);

	void moveFormData(Integer types, Long formDataId, Integer targetCateId);

	JsonNode getSalesmanCustomFields(Long adminId, Integer customType);

	void saveSalesmanCustomFields(Long adminId, Integer customType, String selectType, JsonNode dataArray);

}
