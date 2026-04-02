package com.bubblecloud.biz.oa.service;

import java.util.List;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.entity.SystemStorage;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * 云存储配置（对齐 PHP {@code SystemStorageController}，不含真实云 SDK 调用）。
 *
 * @author qinlei
 * @date 2026/3/30
 */
public interface SystemStorageAdminService extends UpService<SystemStorage> {

	List<SystemStorage> list(Integer type);

	SystemStorage get(Integer id);

	void saveStorage(Integer type, String accessKey, String name, String region, String acl);

	void updateDomain(Integer id, String domain, String cdn);

	void setActiveStatus(Integer id);

	void deleteStorage(Integer id);

	void setUploadType(Integer type);

	Integer getUploadType();

	void saveBasicConfig(JsonNode body);

}
