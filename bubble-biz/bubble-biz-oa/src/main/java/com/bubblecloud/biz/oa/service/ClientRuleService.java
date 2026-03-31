package com.bubblecloud.biz.oa.service;

import java.util.List;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.entity.SystemConfig;
import com.bubblecloud.oa.api.dto.config.ClientRuleApproveSaveDTO;
import com.bubblecloud.oa.api.vo.config.ClientRuleApproveConfigVO;
import com.bubblecloud.oa.api.vo.config.ConfigCateItemVO;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * 客户规则配置业务（对齐 PHP {@code ClientRuleController}）。
 *
 * @author qinlei
 * @date 2026/3/30 下午11:10
 */
public interface ClientRuleService extends UpService<SystemConfig> {

	/**
	 * 客户规则分类列表。
	 */
	List<ConfigCateItemVO> listClientRuleCates();

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
