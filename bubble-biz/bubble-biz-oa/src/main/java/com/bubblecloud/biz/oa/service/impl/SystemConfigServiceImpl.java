package com.bubblecloud.biz.oa.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.bubblecloud.biz.oa.mapper.ApproveMapper;
import com.bubblecloud.biz.oa.util.OaFormRuleFactory;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.dto.config.ClientRuleApproveSaveDTO;
import com.bubblecloud.oa.api.entity.Approve;
import com.bubblecloud.oa.api.vo.config.FirewallConfigVO;
import com.bubblecloud.oa.api.vo.form.OaElFormVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
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

	private static final List<String> CLIENT_RULE_APPROVE_KEYS = List.of("contract_refund_switch",
			"contract_renew_switch", "contract_disburse_switch", "invoicing_switch", "void_invoice_switch");

	private static final Map<String, List<String>> CLIENT_RULE_KEYS_BY_CATEGORY;

	private static final Map<String, String> CLIENT_RULE_KEY_DEFAULTS;

	static {
		Map<String, List<String>> cat = new LinkedHashMap<>();
		cat.put("customer_follow_config",
				List.of("follow_up_switch", "follow_up_status", "follow_up_traded", "follow_up_unsettled"));
		cat.put("customer_sea_config", List.of("return_high_seas_switch", "unsettled_cycle", "unfollowed_cycle",
				"advance_cycle", "client_policy_switch", "unsettled_client_number"));
		cat.put("customer_approve_config", List.copyOf(CLIENT_RULE_APPROVE_KEYS));
		CLIENT_RULE_KEYS_BY_CATEGORY = Collections.unmodifiableMap(cat);
		Map<String, String> def = new HashMap<>();
		def.put("follow_up_switch", "0");
		def.put("follow_up_status", "[]");
		def.put("follow_up_traded", "0");
		def.put("follow_up_unsettled", "0");
		def.put("return_high_seas_switch", "0");
		def.put("unsettled_cycle", "30");
		def.put("unfollowed_cycle", "30");
		def.put("advance_cycle", "5");
		def.put("client_policy_switch", "0");
		def.put("unsettled_client_number", "999");
		def.put("contract_refund_switch", "0");
		def.put("contract_renew_switch", "0");
		def.put("contract_disburse_switch", "0");
		def.put("invoicing_switch", "0");
		def.put("void_invoice_switch", "0");
		CLIENT_RULE_KEY_DEFAULTS = Collections.unmodifiableMap(def);
	}

	private static final Map<String, Integer> APPROVE_TYPE_BY_SWITCH_KEY;

	private static final Map<String, String> APPROVE_ZERO_LABEL_BY_SWITCH_KEY;

	static {
		Map<String, Integer> t = new HashMap<>();
		t.put("contract_refund_switch", 6);
		t.put("contract_renew_switch", 7);
		t.put("contract_disburse_switch", 8);
		t.put("invoicing_switch", 9);
		t.put("void_invoice_switch", 10);
		APPROVE_TYPE_BY_SWITCH_KEY = Collections.unmodifiableMap(t);
		Map<String, String> z = new HashMap<>();
		z.put("contract_refund_switch", "无需审批（直接生成收入账目）");
		z.put("contract_renew_switch", "无需审批（直接生成收入账目）");
		z.put("contract_disburse_switch", "无需审批（直接生成支出账目）");
		z.put("invoicing_switch", "无需审批（财务直接核对开票）");
		z.put("void_invoice_switch", "无需审批（直接作废发票）");
		APPROVE_ZERO_LABEL_BY_SWITCH_KEY = Collections.unmodifiableMap(z);
	}

	private static final Map<String, String> APPROVE_TITLE_FALLBACK = Map.of("contract_refund_switch", "合同回款",
			"contract_renew_switch", "合同续费", "contract_disburse_switch", "合同支出", "invoicing_switch", "开具发票",
			"void_invoice_switch", "作废发票");

	private static final Map<String, String> CLIENT_RULE_KEY_TITLES;

	static {
		Map<String, String> titles = new HashMap<>();
		titles.put("follow_up_switch", "客户跟进提醒");
		titles.put("follow_up_status", "客户状态");
		titles.put("follow_up_traded", "客户状态已成交提醒周期");
		titles.put("follow_up_unsettled", "客户状态暂未成交提醒周期");
		titles.put("return_high_seas_switch", "自动退回公海规则");
		titles.put("unsettled_cycle", "退回客户公海周期(暂未成交)");
		titles.put("unfollowed_cycle", "未跟进退回公海(暂未成交)");
		titles.put("advance_cycle", "客户退回公海提醒提前");
		titles.put("client_policy_switch", "客户保单规则");
		titles.put("unsettled_client_number", "暂未成交客户数量设置");
		titles.putAll(APPROVE_TITLE_FALLBACK);
		CLIENT_RULE_KEY_TITLES = Collections.unmodifiableMap(titles);
	}

	private final ObjectMapper objectMapper;

	private final ApproveMapper approveMapper;

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
	public String getConfigRawValue(String configKey) {
		if (StrUtil.isBlank(configKey)) {
			return "";
		}
		SystemConfig c = this.getOne(
				Wrappers.lambdaQuery(SystemConfig.class).eq(SystemConfig::getConfigKey, configKey).last("LIMIT 1"),
				false);
		if (ObjectUtil.isNull(c) || ObjectUtil.isNull(c.getValue())) {
			return "";
		}
		return c.getValue();
	}

	@Override
	public JsonNode getClientRuleApprovePayload(Integer form) {
		boolean formMode = ObjectUtil.isNull(form) || form != 0;
		if (formMode) {
			OaElFormVO vo = buildClientRuleApproveElForm();
			return objectMapper.valueToTree(vo);
		}
		return buildClientRuleApprovePlain();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
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
		String c = category.trim();
		List<String> keys = CLIENT_RULE_KEYS_BY_CATEGORY.get(c);
		if (CollUtil.isEmpty(keys)) {
			return objectMapper.createObjectNode();
		}
		ObjectNode out = objectMapper.createObjectNode();
		for (String key : keys) {
			String def = CLIENT_RULE_KEY_DEFAULTS.getOrDefault(key, "");
			SystemConfig row = baseMapper.selectOne(Wrappers.lambdaQuery(SystemConfig.class)
				.eq(SystemConfig::getConfigKey, key)
				.eq(SystemConfig::getEntid, 0)
				.last("LIMIT 1"));
			String val = (ObjectUtil.isNotNull(row) && ObjectUtil.isNotNull(row.getValue())) ? row.getValue() : def;
			putClientRuleConfigLeaf(out, key, val);
		}
		return out;
	}

	@Override
	public void saveConfigByCategory(String category, JsonNode body) {
		if (StrUtil.isBlank(category) || ObjectUtil.isNull(body) || !body.isObject()) {
			return;
		}
		String c = category.trim();
		List<String> allowedList = CLIENT_RULE_KEYS_BY_CATEGORY.get(c);
		if (CollUtil.isEmpty(allowedList)) {
			return;
		}
		Set<String> allowed = new HashSet<>(allowedList);
		body.fields().forEachRemaining(e -> {
			String k = e.getKey();
			if (StrUtil.isBlank(k) || !allowed.contains(k)) {
				return;
			}
			String val = jsonNodeToString(e.getValue());
			SystemConfig row = baseMapper.selectOne(Wrappers.lambdaQuery(SystemConfig.class)
				.eq(SystemConfig::getConfigKey, k)
				.eq(SystemConfig::getEntid, 0)
				.last("LIMIT 1"));
			if (ObjectUtil.isNotNull(row)) {
				row.setValue(val);
				if (!Objects.equals(c, row.getCategory())) {
					row.setCategory(c);
				}
				baseMapper.updateById(row);
			}
			else {
				SystemConfig n = new SystemConfig();
				n.setCategory(c);
				n.setConfigKey(k);
				n.setKeyName(CLIENT_RULE_KEY_TITLES.getOrDefault(k, k));
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

	private void putClientRuleConfigLeaf(ObjectNode out, String key, String val) {
		if ("follow_up_status".equals(key)) {
			String u = StrUtil.isBlank(val) ? "[]" : val.trim();
			if (u.startsWith("[")) {
				try {
					out.set(key, objectMapper.readTree(u));
					return;
				}
				catch (JsonProcessingException ignored) {
				}
			}
		}
		out.put(key, StrUtil.nullToEmpty(val));
	}

	private OaElFormVO buildClientRuleApproveElForm() {
		ArrayNode rules = objectMapper.createArrayNode();
		for (String key : CLIENT_RULE_APPROVE_KEYS) {
			SystemConfig row = baseMapper.selectOne(
					Wrappers.lambdaQuery(SystemConfig.class).eq(SystemConfig::getConfigKey, key).last("LIMIT 1"));
			String titleBase = resolveApproveSwitchTitle(row, key);
			String title = StrUtil.endWith(titleBase, "：") ? titleBase : titleBase + "：";
			int cur = loadIntConfig(key, 0);
			String zeroLabel = APPROVE_ZERO_LABEL_BY_SWITCH_KEY.getOrDefault(key, "");
			Integer types = APPROVE_TYPE_BY_SWITCH_KEY.get(key);
			List<Approve> opts = ObjectUtil.isNull(types) ? List.of() : listApproveExamineOne(types);
			rules.add(OaFormRuleFactory.approveSelect(objectMapper, key, title, cur, zeroLabel, opts));
		}
		OaElFormVO vo = new OaElFormVO();
		vo.setTitle("修改配置");
		vo.setMethod("put");
		vo.setAction("/config/client_rule/approve");
		vo.setRule(rules);
		return vo;
	}

	private String resolveApproveSwitchTitle(SystemConfig row, String key) {
		if (ObjectUtil.isNotNull(row) && StrUtil.isNotBlank(row.getKeyName())) {
			return row.getKeyName();
		}
		return APPROVE_TITLE_FALLBACK.getOrDefault(key, key);
	}

	private ObjectNode buildClientRuleApprovePlain() {
		ObjectNode out = objectMapper.createObjectNode();
		for (String key : CLIENT_RULE_APPROVE_KEYS) {
			SystemConfig row = baseMapper.selectOne(
					Wrappers.lambdaQuery(SystemConfig.class).eq(SystemConfig::getConfigKey, key).last("LIMIT 1"));
			String raw = "";
			if (ObjectUtil.isNotNull(row) && ObjectUtil.isNotNull(row.getValue())) {
				raw = row.getValue().trim();
			}
			if (StrUtil.isBlank(raw)) {
				Integer types = APPROVE_TYPE_BY_SWITCH_KEY.get(key);
				Long draftId = ObjectUtil.isNull(types) ? null : firstDraftApproveId(types);
				out.put(key, ObjectUtil.isNull(draftId) ? 0 : draftId.intValue());
			}
			else {
				out.put(key, NumberUtil.parseInt(raw, 0));
			}
		}
		return out;
	}

	private List<Approve> listApproveExamineOne(Integer types) {
		return approveMapper.selectList(Wrappers.lambdaQuery(Approve.class)
			.eq(Approve::getTypes, types)
			.eq(Approve::getExamine, 1)
			.orderByAsc(Approve::getSort)
			.orderByAsc(Approve::getId));
	}

	private Long firstDraftApproveId(Integer types) {
		Approve one = approveMapper.selectOne(Wrappers.lambdaQuery(Approve.class)
			.eq(Approve::getTypes, types)
			.eq(Approve::getExamine, 0)
			.orderByAsc(Approve::getId)
			.last("LIMIT 1"));
		return ObjectUtil.isNull(one) ? null : one.getId();
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
