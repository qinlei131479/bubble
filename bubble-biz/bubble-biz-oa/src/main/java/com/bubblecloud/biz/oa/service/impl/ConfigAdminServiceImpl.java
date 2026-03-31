package com.bubblecloud.biz.oa.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.SystemConfigMapper;
import com.bubblecloud.biz.oa.service.ConfigAdminService;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.SystemConfig;
import com.bubblecloud.oa.api.vo.config.ConfigCateItemVO;
import com.bubblecloud.oa.api.vo.config.FirewallConfigVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * OA 系统配置管理实现。
 *
 * @author qinlei
 * @date 2026/3/30 下午9:15
 */
@Service
@RequiredArgsConstructor
public class ConfigAdminServiceImpl extends UpServiceImpl<SystemConfigMapper, SystemConfig> implements ConfigAdminService {

	private static final String CAT_WORK_BENCH = "work_bench";

	private static final String CAT_FIREWALL = "firewall";

	private final ObjectMapper objectMapper;

	@Override
	public List<SystemConfig> listWorkBenchConfigs(int entid) {
		return baseMapper.selectList(Wrappers.lambdaQuery(SystemConfig.class)
			.eq(SystemConfig::getCategory, CAT_WORK_BENCH)
			.eq(SystemConfig::getEntid, entid)
			.eq(SystemConfig::getIsShow, 1)
			.orderByAsc(SystemConfig::getSort)
			.orderByAsc(SystemConfig::getId));
	}

	@Override
	public void saveWorkBench(int entid, JsonNode body) {
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
		vo.setFirewallSwitch(ObjectUtil.isNotNull(sw) && ObjectUtil.isNotNull(sw.getValue()) ? parseIntSafe(sw.getValue(), 0) : 0);
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
	public void saveFirewallConfig(int firewallSwitch, List<String> firewallContent) {
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

	@Override
	public List<ConfigCateItemVO> listConfigCateBase() {
		List<ConfigCateItemVO> list = new ArrayList<>(4);
		list.add(new ConfigCateItemVO("系统配置", "system_config"));
		list.add(new ConfigCateItemVO("存储配置", "storage_config"));
		list.add(new ConfigCateItemVO("一号通配置", "yiht_config"));
		list.add(new ConfigCateItemVO("App通知配置", "push_config"));
		return list;
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

	private static int parseIntSafe(String s, int def) {
		try {
			return Integer.parseInt(s.trim());
		}
		catch (Exception e) {
			return def;
		}
	}

}
