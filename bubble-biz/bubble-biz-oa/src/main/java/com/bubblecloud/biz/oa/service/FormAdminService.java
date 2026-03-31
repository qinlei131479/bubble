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

	List<FormCateListItemVO> listByTypes(int types);

	long saveCate(int types, String title, Integer sort, int status);

	void updateCate(long id, String title, Integer sort, int status);

	void deleteCate(long id);

	void updateCateStatus(long id, int status);

	void saveFormData(int types, JsonNode body);

	void moveFormData(int types, long formDataId, int targetCateId);

	JsonNode getSalesmanCustomFields(long adminId, int customType);

	void saveSalesmanCustomFields(long adminId, int customType, String selectType, JsonNode dataArray);

}
