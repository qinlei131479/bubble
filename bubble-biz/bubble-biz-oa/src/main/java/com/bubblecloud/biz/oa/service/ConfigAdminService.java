package com.bubblecloud.biz.oa.service;

import java.util.List;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.entity.SystemConfig;
import com.bubblecloud.oa.api.vo.config.ConfigCateItemVO;
import com.bubblecloud.oa.api.vo.config.FirewallConfigVO;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * OA 企业后台系统配置服务接口，对齐 PHP {@code ent/config} 工作台、分类与防火墙等能力。
 *
 * @author qinlei
 * @date 2026/3/30 下午9:15
 */
public interface ConfigAdminService extends UpService<SystemConfig> {

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

	/**
	 * 配置分类列表（系统配置/存储等基础项，对齐 PHP ConfigCate）。
	 */
	List<ConfigCateItemVO> listConfigCateBase();

}
