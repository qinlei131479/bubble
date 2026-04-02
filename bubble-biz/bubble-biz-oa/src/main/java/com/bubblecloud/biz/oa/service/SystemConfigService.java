package com.bubblecloud.biz.oa.service;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.dto.ConfigQueryDTO;
import com.bubblecloud.oa.api.dto.config.ClientRuleApproveSaveDTO;
import com.bubblecloud.oa.api.entity.SystemConfig;
import com.bubblecloud.oa.api.vo.ConfigVO;
import com.bubblecloud.oa.api.vo.config.ClientRuleApproveConfigVO;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * eb_system_config 系统配置服务。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
public interface SystemConfigService extends UpService<SystemConfig> {

	/**
	 * 按类型读取配置为键值 VO。
	 *
	 * @param dto 查询条件
	 * @return 配置 VO
	 */
	ConfigVO config(ConfigQueryDTO dto);

	/**
	 * 是否开放注册（registration_open）。
	 */
	boolean isRegistrationOpen();


	/**
	 * 客户审批开关配置。
	 */
	ClientRuleApproveConfigVO getApproveConfig(Integer form);

	/**
	 * 保存客户审批开关。
	 */
	void saveApproveConfig(ClientRuleApproveSaveDTO dto);

	/**
	 * 按分类读取配置为 JSON 对象（动态键，与 PHP sys_more 结构一致）。
	 */
	JsonNode getConfigByCategory(String category);

	/**
	 * 按分类保存配置（请求体为 JSON 对象）。
	 */
	void saveConfigByCategory(String category, JsonNode body);
}
