package com.bubblecloud.biz.oa.service.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.EnterpriseMapper;
import com.bubblecloud.biz.oa.mapper.SystemConfigMapper;
import com.bubblecloud.biz.oa.service.SiteService;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.Enterprise;
import com.bubblecloud.oa.api.entity.SystemConfig;
import com.bubblecloud.oa.api.vo.SiteVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 站点展示配置实现。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@Service
@RequiredArgsConstructor
public class SiteServiceImpl extends UpServiceImpl<SystemConfigMapper, SystemConfig> implements SiteService {

	private static final String APP_VERSION = "3.9.2";

	private final EnterpriseMapper enterpriseMapper;

	private final ObjectMapper objectMapper;

	@Override
	public SiteVO site() {
		List<SystemConfig> configs = baseMapper.selectList(Wrappers.lambdaQuery(SystemConfig.class));
		Map<String, String> configMap = configs.stream()
			.filter(c -> ObjectUtil.isNotNull(c.getConfigKey()))
			.collect(Collectors.toMap(SystemConfig::getConfigKey, c -> ObjectUtil.isNull(c.getValue()) ? "" : c.getValue(),
					(a, b) -> b, LinkedHashMap::new));

		Enterprise ent = enterpriseMapper
			.selectOne(Wrappers.lambdaQuery(Enterprise.class).eq(Enterprise::getId, 1L).eq(Enterprise::getStatus, 1));

		SiteVO vo = new SiteVO();
		vo.setSiteRecordNumber(configMap.getOrDefault("site_record_number", ""));
		vo.setSiteAddress(buildAddress(ent));
		vo.setSiteTel(configMap.getOrDefault("site_tel", ""));
		vo.setSiteLogo(ObjectUtil.isNull(ent) || ObjectUtil.isNull(ent.getLogo()) ? "" : ent.getLogo());
		vo.setPasswordType(parsePasswordType(configMap.get("login_password_type")));
		vo.setPasswordLength(parseInteger(configMap.get("login_password_length"), 0));
		vo.setVersionName(APP_VERSION);
		vo.setGlobalAttachSize(parseInteger(configMap.get("global_attach_size"), 0));
		return vo;
	}

	private String buildAddress(Enterprise enterprise) {
		if (ObjectUtil.isNull(enterprise)) {
			return "";
		}
		return String.valueOf(ObjectUtil.isNull(enterprise.getProvince()) ? "" : enterprise.getProvince())
				+ (ObjectUtil.isNull(enterprise.getCity()) ? "" : enterprise.getCity())
				+ (ObjectUtil.isNull(enterprise.getArea()) ? "" : enterprise.getArea())
				+ (ObjectUtil.isNull(enterprise.getAddress()) ? "" : enterprise.getAddress());
	}

	private String parsePasswordType(String raw) {
		if (StrUtil.isBlank(raw)) {
			return "02";
		}
		try {
			List<Integer> values = objectMapper.readValue(raw, new TypeReference<List<Integer>>() {
			});
			List<Integer> sorted = values.stream().filter(Objects::nonNull).sorted().toList();
			if (!sorted.isEmpty()) {
				return sorted.stream().map(String::valueOf).collect(Collectors.joining());
			}
		}
		catch (Exception ignored) {
			// fallback 到逗号分隔
		}
		List<Long> parsed = parseIdList(raw);
		List<Integer> values = parsed.stream().map(Long::intValue).sorted().toList();
		if (values.isEmpty()) {
			return "02";
		}
		return values.stream().map(String::valueOf).collect(Collectors.joining());
	}

	private Integer parseInteger(String raw, int defaultValue) {
		try {
			return StrUtil.isNotBlank(raw) ? Integer.parseInt(raw) : defaultValue;
		}
		catch (NumberFormatException ex) {
			return defaultValue;
		}
	}

	private List<Long> parseIdList(String raw) {
		if (StrUtil.isBlank(raw)) {
			return List.of();
		}
		String value = raw.trim();
		try {
			if (value.startsWith("[") && value.endsWith("]")) {
				List<Long> parsed = objectMapper.readValue(value, new TypeReference<List<Long>>() {
				});
				return parsed.stream().filter(Objects::nonNull).toList();
			}
		}
		catch (Exception ignored) {
			// fallback
		}
		String normalized = value.replace("[", "").replace("]", "").replace("\"", "");
		if (StrUtil.isBlank(normalized)) {
			return List.of();
		}
		return java.util.Arrays.stream(normalized.split(","))
			.map(String::trim)
			.filter(StrUtil::isNotBlank)
			.map(item -> {
				try {
					return Long.parseLong(item);
				}
				catch (NumberFormatException ex) {
					return null;
				}
			})
			.filter(Objects::nonNull)
			.toList();
	}

}
