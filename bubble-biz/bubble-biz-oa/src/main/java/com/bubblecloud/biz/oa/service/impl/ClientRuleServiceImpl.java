package com.bubblecloud.biz.oa.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.SystemConfigMapper;
import com.bubblecloud.biz.oa.service.ClientRuleService;
import com.bubblecloud.oa.api.dto.config.ClientRuleApproveSaveDTO;
import com.bubblecloud.oa.api.entity.SystemConfig;
import com.bubblecloud.oa.api.vo.config.ClientRuleApproveConfigVO;
import com.bubblecloud.oa.api.vo.config.ConfigCateItemVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 客户规则配置实现。
 *
 * @author qinlei
 * @date 2026/3/30 下午11:10
 */
@Service
@RequiredArgsConstructor
public class ClientRuleServiceImpl implements ClientRuleService {

	private final SystemConfigMapper systemConfigMapper;

	private final ObjectMapper objectMapper;

	@Override
	public List<ConfigCateItemVO> listClientRuleCates() {
		List<ConfigCateItemVO> list = new ArrayList<>(3);
		list.add(new ConfigCateItemVO("客户跟进配置", "customer_follow_config"));
		list.add(new ConfigCateItemVO("客户公海配置", "customer_sea_config"));
		list.add(new ConfigCateItemVO("客户审批配置", "customer_approve_config"));
		return list;
	}

	@Override
	@SuppressWarnings("unused")
	public ClientRuleApproveConfigVO getApproveConfig(Integer form) {
		ClientRuleApproveConfigVO vo = new ClientRuleApproveConfigVO();
		vo.setContractRefundSwitch(loadIntConfig("contract_refund_switch", 0));
		vo.setContractRenewSwitch(loadIntConfig("contract_renew_switch", 0));
		vo.setContractDisburseSwitch(loadIntConfig("contract_disburse_switch", 0));
		vo.setInvoicingSwitch(loadIntConfig("invoicing_switch", 0));
		vo.setVoidInvoiceSwitch(loadIntConfig("void_invoice_switch", 0));
		return vo;
	}

	@Override
	public void saveApproveConfig(ClientRuleApproveSaveDTO dto) {
		if (dto == null) {
			return;
		}
		putIfPresent("contract_refund_switch", dto.getContractRefundSwitch());
		putIfPresent("contract_renew_switch", dto.getContractRenewSwitch());
		putIfPresent("contract_disburse_switch", dto.getContractDisburseSwitch());
		putIfPresent("invoicing_switch", dto.getInvoicingSwitch());
		putIfPresent("void_invoice_switch", dto.getVoidInvoiceSwitch());
	}

	private void putIfPresent(String key, Integer val) {
		if (val == null) {
			return;
		}
		upsertConfigKey(key, String.valueOf(val));
	}

	@Override
	public JsonNode getConfigByCategory(String category) {
		if (!StringUtils.hasText(category)) {
			return objectMapper.createObjectNode();
		}
		List<SystemConfig> rows = systemConfigMapper.selectList(Wrappers.lambdaQuery(SystemConfig.class)
			.eq(SystemConfig::getCategory, category)
			.eq(SystemConfig::getEntid, 0));
		ObjectNode out = objectMapper.createObjectNode();
		for (SystemConfig r : rows) {
			if (r.getConfigKey() != null) {
				out.put(r.getConfigKey(), r.getValue() == null ? "" : r.getValue());
			}
		}
		return out;
	}

	@Override
	public void saveConfigByCategory(String category, JsonNode body) {
		if (!StringUtils.hasText(category) || body == null || !body.isObject()) {
			return;
		}
		body.fields().forEachRemaining(e -> {
			String k = e.getKey();
			if (!StringUtils.hasText(k)) {
				return;
			}
			String val = jsonNodeToString(e.getValue());
			SystemConfig row = systemConfigMapper.selectOne(Wrappers.lambdaQuery(SystemConfig.class)
				.eq(SystemConfig::getConfigKey, k)
				.eq(SystemConfig::getEntid, 0)
				.last("LIMIT 1"));
			if (row != null) {
				row.setValue(val);
				if (!Objects.equals(category, row.getCategory())) {
					row.setCategory(category);
				}
				systemConfigMapper.updateById(row);
			}
			else {
				SystemConfig n = new SystemConfig();
				n.setCategory(category);
				n.setConfigKey(k);
				n.setKeyName(k);
				n.setValue(val);
				n.setEntid(0);
				n.setType("text");
				n.setInputType("input");
				n.setSort(0);
				n.setIsShow(0);
				systemConfigMapper.insert(n);
			}
		});
	}

	private static String jsonNodeToString(JsonNode n) {
		if (n == null || n.isNull()) {
			return "";
		}
		if (n.isValueNode()) {
			return n.asText();
		}
		return n.toString();
	}

	private int loadIntConfig(String key, int def) {
		SystemConfig row = systemConfigMapper
			.selectOne(Wrappers.lambdaQuery(SystemConfig.class).eq(SystemConfig::getConfigKey, key).last("LIMIT 1"));
		if (row == null || row.getValue() == null) {
			return def;
		}
		try {
			return Integer.parseInt(row.getValue().trim());
		}
		catch (NumberFormatException e) {
			return def;
		}
	}

	private void upsertConfigKey(String key, String value) {
		SystemConfig row = systemConfigMapper
			.selectOne(Wrappers.lambdaQuery(SystemConfig.class).eq(SystemConfig::getConfigKey, key).last("LIMIT 1"));
		if (row != null) {
			row.setValue(value);
			systemConfigMapper.updateById(row);
		}
		else {
			SystemConfig n = new SystemConfig();
			n.setConfigKey(key);
			n.setKeyName(key);
			n.setValue(value);
			n.setCategory("customer");
			n.setEntid(0);
			n.setType("text");
			n.setInputType("input");
			systemConfigMapper.insert(n);
		}
	}

}
