package com.bubblecloud.biz.oa.service;

import java.util.List;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.entity.SystemStorage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 云存储配置（对齐 PHP {@code SystemStorageController}，不含真实云 SDK 调用）。
 *
 * @author qinlei
 * @date 2026/3/30
 */
public interface SystemStorageService extends UpService<SystemStorage> {

	List<SystemStorage> listStorages(Integer type);

	void updateDomain(SystemStorage dto);

	void updateStatus(Long id);

	void setUploadType(Integer type);

	Integer getUploadType();

	void saveBasicConfig(JsonNode body);

	void saveCredentialConfig(JsonNode body);

	ObjectNode storageMethodDetail();

	boolean deleteStorage(Long id);

}
