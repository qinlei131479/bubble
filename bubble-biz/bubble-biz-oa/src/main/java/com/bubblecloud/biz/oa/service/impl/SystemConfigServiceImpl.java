package com.bubblecloud.biz.oa.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.dto.config.ClientRuleApproveSaveDTO;
import com.bubblecloud.oa.api.vo.config.ClientRuleApproveConfigVO;
import com.bubblecloud.oa.api.vo.config.ConfigCateItemVO;
import com.bubblecloud.oa.api.vo.config.FirewallConfigVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.SystemConfigMapper;
import com.bubblecloud.biz.oa.service.SystemConfigService;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.dto.ConfigQueryDTO;
import com.bubblecloud.oa.api.entity.SystemConfig;
import com.bubblecloud.oa.api.vo.ConfigVO;
import org.springframework.stereotype.Service;
import cn.hutool.core.util.ObjectUtil;

/**
 * eb_system_config 系统配置服务实现。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@Service
@RequiredArgsConstructor
public class SystemConfigServiceImpl extends UpServiceImpl<SystemConfigMapper, SystemConfig>
		implements SystemConfigService {

	private static final String CAT_WORK_BENCH = "work_bench";

	private static final String CAT_FIREWALL = "firewall";

	private final ObjectMapper objectMapper;

	@Override
	public ConfigVO config(ConfigQueryDTO dto) {
		List<SystemConfig> list = this
			.list(Wrappers.lambdaQuery(SystemConfig.class).eq(SystemConfig::getCategory, dto.getType()));
		ConfigVO vo = new ConfigVO();
		for (SystemConfig c : list) {
			if (ObjectUtil.isNotNull(c.getConfigKey())) {
				vo.getEntries().put(c.getConfigKey(), ObjectUtil.isNull(c.getValue()) ? "" : c.getValue());
			}
		}
		return vo;
	}

	@Override
	public boolean isRegistrationOpen() {
		SystemConfig reg = this.getOne(Wrappers.lambdaQuery(SystemConfig.class)
			.eq(SystemConfig::getConfigKey, "registration_open")
			.last("LIMIT 1"), false);
		return ObjectUtil.isNotNull(reg) && ("1".equals(reg.getValue()) || "true".equalsIgnoreCase(reg.getValue()));
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
		if (ObjectUtil.isNull(dto)) {
			return;
		}
		putIfPresent("contract_refund_switch", dto.getContractRefundSwitch());
		putIfPresent("contract_renew_switch", dto.getContractRenewSwitch());
		putIfPresent("contract_disburse_switch", dto.getContractDisburseSwitch());
		putIfPresent("invoicing_switch", dto.getInvoicingSwitch());
		putIfPresent("void_invoice_switch", dto.getVoidInvoiceSwitch());
	}

	private void putIfPresent(String key, Integer val) {
		if (ObjectUtil.isNull(val)) {
			return;
		}
		upsertConfigKey(key, String.valueOf(val));
	}

	@Override
	public JsonNode getConfigByCategory(String category) {
		if (StrUtil.isBlank(category)) {
			return objectMapper.createObjectNode();
		}
		List<SystemConfig> rows = baseMapper.selectList(Wrappers.lambdaQuery(SystemConfig.class)
			.eq(SystemConfig::getCategory, category)
			.eq(SystemConfig::getEntid, 0));
		ObjectNode out = objectMapper.createObjectNode();
		for (SystemConfig r : rows) {
			if (ObjectUtil.isNotNull(r.getConfigKey())) {
				out.put(r.getConfigKey(), ObjectUtil.isNull(r.getValue()) ? "" : r.getValue());
			}
		}
		return out;
	}

	@Override
	public void saveConfigByCategory(String category, JsonNode body) {
		if (StrUtil.isBlank(category) || ObjectUtil.isNull(body) || !body.isObject()) {
			return;
		}
		body.fields().forEachRemaining(e -> {
			String k = e.getKey();
			if (StrUtil.isBlank(k)) {
				return;
			}
			String val = jsonNodeToString(e.getValue());
			SystemConfig row = baseMapper.selectOne(Wrappers.lambdaQuery(SystemConfig.class)
				.eq(SystemConfig::getConfigKey, k)
				.eq(SystemConfig::getEntid, 0)
				.last("LIMIT 1"));
			if (ObjectUtil.isNotNull(row)) {
				row.setValue(val);
				if (!Objects.equals(category, row.getCategory())) {
					row.setCategory(category);
				}
				baseMapper.updateById(row);
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
				baseMapper.insert(n);
			}
		});
	}

	private static String jsonNodeToString(JsonNode n) {
		if (ObjectUtil.isNull(n) || n.isNull()) {
			return "";
		}
		if (n.isValueNode()) {
			return n.asText();
		}
		return n.toString();
	}

	private int loadIntConfig(String key, int def) {
		SystemConfig row = baseMapper
			.selectOne(Wrappers.lambdaQuery(SystemConfig.class).eq(SystemConfig::getConfigKey, key).last("LIMIT 1"));
		if (ObjectUtil.isNull(row) || ObjectUtil.isNull(row.getValue())) {
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
		SystemConfig row = baseMapper
			.selectOne(Wrappers.lambdaQuery(SystemConfig.class).eq(SystemConfig::getConfigKey, key).last("LIMIT 1"));
		if (ObjectUtil.isNotNull(row)) {
			row.setValue(value);
			baseMapper.updateById(row);
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
			baseMapper.insert(n);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(SystemConfig req) {
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(SystemConfig req) {
		return super.update(req);
	}

	/**
	 * 企业配置
	 **/

	@Override
	public List<SystemConfig> listWorkBenchConfigs(Integer entid) {
		return baseMapper.selectList(Wrappers.lambdaQuery(SystemConfig.class)
			.eq(SystemConfig::getCategory, CAT_WORK_BENCH)
			.eq(SystemConfig::getEntid, entid)
			.eq(SystemConfig::getIsShow, 1)
			.orderByAsc(SystemConfig::getSort)
			.orderByAsc(SystemConfig::getId));
	}

	@Override
	public void saveWorkBench(Integer entid, JsonNode body) {
		if (ObjectUtil.isNull(body) || !body.isObject()) {
			return;
		}
		body.fields().forEachRemaining(e -> {
			String key = e.getKey();
			JsonNode valNode = e.getValue();
			String val = jsonNodeToString(valNode);
			baseMapper.update(null,
					Wrappers.lambdaUpdate(SystemConfig.class)
						.eq(SystemConfig::getCategory, CAT_WORK_BENCH)
						.eq(SystemConfig::getEntid, entid)
						.eq(SystemConfig::getConfigKey, key)
						.set(SystemConfig::getValue, val));
		});
	}

	@Override
	public List<SystemConfig> listConfigRowsByCategory(String category) {
		if (StrUtil.isBlank(category)) {
			return List.of();
		}
		return baseMapper.selectList(Wrappers.lambdaQuery(SystemConfig.class)
			.eq(SystemConfig::getCategory, category)
			.eq(SystemConfig::getEntid, 0)
			.orderByAsc(SystemConfig::getSort)
			.orderByAsc(SystemConfig::getId));
	}

	@Override
	public void updateAllByCategory(String category, JsonNode body) {
		if (StrUtil.isBlank(category) || ObjectUtil.isNull(body) || !body.isObject()) {
			return;
		}
		List<SystemConfig> rows = baseMapper
			.selectList(Wrappers.lambdaQuery(SystemConfig.class).eq(SystemConfig::getCategory, category));
		body.fields().forEachRemaining(e -> {
			String k = e.getKey();
			String val = jsonNodeToString(e.getValue());
			for (SystemConfig row : rows) {
				if (k.equals(row.getConfigKey())) {
					row.setValue(val);
					baseMapper.updateById(row);
					return;
				}
			}
		});
	}

	@Override
	public FirewallConfigVO getFirewallConfig() {
		FirewallConfigVO vo = new FirewallConfigVO();
		SystemConfig sw = baseMapper.selectOne(Wrappers.lambdaQuery(SystemConfig.class)
			.eq(SystemConfig::getConfigKey, "firewall_switch")
			.last("LIMIT 1"));
		vo.setFirewallSwitch(NumberUtil.parseInt(sw.getValue(), 0));
		SystemConfig fc = baseMapper.selectOne(Wrappers.lambdaQuery(SystemConfig.class)
			.eq(SystemConfig::getConfigKey, "firewall_content")
			.last("LIMIT 1"));
		List<String> rules = new ArrayList<>();
		if (ObjectUtil.isNotNull(fc) && StrUtil.isNotBlank(fc.getValue())) {
			try {
				rules = objectMapper.readValue(fc.getValue(),
						objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
			}
			catch (Exception ignored) {
				rules = new ArrayList<>();
			}
		}
		vo.setFirewallContent(rules);
		return vo;
	}

	@Override
	public void saveFirewallConfig(Integer firewallSwitch, List<String> firewallContent) {
		if (ObjectUtil.isNotNull(firewallContent)) {
			for (String pattern : firewallContent) {
				if (ObjectUtil.isNull(pattern) || pattern.isEmpty()) {
					continue;
				}
				try {
					java.util.regex.Pattern.compile(pattern);
				}
				catch (Exception ex) {
					throw new IllegalArgumentException("防火墙配置格式错误,必须为正则表达式");
				}
			}
		}
		upsertFirewallRow("firewall_switch", "防火墙开关", String.valueOf(firewallSwitch), CAT_FIREWALL);
		String json;
		try {
			json = objectMapper.writeValueAsString(ObjectUtil.isNull(firewallContent) ? List.of() : firewallContent);
		}
		catch (JsonProcessingException e) {
			json = "[]";
		}
		upsertFirewallRow("firewall_content", "防火墙设置", json, CAT_FIREWALL);
	}

	private void upsertFirewallRow(String key, String keyName, String value, String category) {
		SystemConfig row = getBaseMapper()
			.selectOne(Wrappers.lambdaQuery(SystemConfig.class).eq(SystemConfig::getConfigKey, key).last("LIMIT 1"));
		if (ObjectUtil.isNotNull(row)) {
			row.setValue(value);
			getBaseMapper().updateById(row);
		}
		else {
			SystemConfig n = new SystemConfig();
			n.setCategory(category);
			n.setConfigKey(key);
			n.setKeyName(keyName);
			n.setValue(value);
			n.setEntid(0);
			n.setType("text");
			n.setInputType("input");
			getBaseMapper().insert(n);
		}
	}

}
