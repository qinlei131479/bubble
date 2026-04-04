package com.bubblecloud.biz.oa.service;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.dto.ConfigQueryDTO;
import com.bubblecloud.oa.api.dto.config.ClientRuleApproveSaveDTO;
import com.bubblecloud.oa.api.entity.SystemConfig;
import com.bubblecloud.oa.api.vo.ConfigVO;
import com.bubblecloud.oa.api.vo.config.ClientRuleApproveConfigVO;
import com.bubblecloud.oa.api.vo.config.FirewallConfigVO;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/**
 * eb_system_config 系统配置服务。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
public interface SystemConfigService extends UpService<SystemConfig> {

	/**
	 * 按类型读取配置为键值 VO。
	 * @param dto 查询条件
	 * @return 配置 VO
	 */
	ConfigVO config(ConfigQueryDTO dto);

	/**
	 * 是否开放注册（registration_open）。
	 */
	boolean isRegistrationOpen();

	/**
	 * 按 config key 读取单条 value，不存在返回空串。
	 */
	String getConfigRawValue(String configKey);

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

	/**
	 * 工作台配置行（企业维度，category=work_bench）。
	 */
	List<SystemConfig> listWorkBenchConfigs(Integer entid);

	/**
	 * 保存工作台配置键值（请求体为 JSON 对象，与 PHP postMore 一致）。
	 */
	void saveWorkBench(Integer entid, JsonNode body);

	/**
	 * 某分类下全局配置行（通常 entid=0），用于「修改配置」表单数据。
	 */
	List<SystemConfig> listConfigRowsByCategory(String category);

	/**
	 * 按分类批量更新配置值（键为 config key）。
	 */
	void updateAllByCategory(String category, JsonNode body);

	/**
	 * 防火墙开关与规则内容。
	 */
	FirewallConfigVO getFirewallConfig();

	/**
	 * 保存防火墙配置。
	 */
	void saveFirewallConfig(Integer firewallSwitch, List<String> firewallContent);

}
