package com.bubblecloud.biz.oa.service;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.entity.SystemStorage;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * 云存储配置（对齐 PHP {@code SystemStorageController}，不含真实云 SDK 调用）。
 *
 * @author qinlei
 * @date 2026/3/30
 */
public interface SystemStorageService extends UpService<SystemStorage> {

	void updateDomain(SystemStorage dto);

	void updateStatus(Long id);

	void setUploadType(Integer type);

	Integer getUploadType();

	void saveBasicConfig(JsonNode body);

}
