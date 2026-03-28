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
import com.bubblecloud.oa.api.entity.Enterprise;
import com.bubblecloud.oa.api.entity.SystemConfig;
import com.bubblecloud.oa.api.vo.SiteVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 站点展示配置实现。
 *
 * @author qinlei
 */
@Service
@RequiredArgsConstructor
public class SiteServiceImpl implements SiteService {

	private static final String APP_VERSION = "3.9.2";

	private final SystemConfigMapper systemConfigMapper;

	private final EnterpriseMapper enterpriseMapper;

	private final ObjectMapper objectMapper;

	@Override
	public SiteVO site() {
		List<SystemConfig> configs = systemConfigMapper
				.selectList(Wrappers.lambdaQuery(SystemConfig.class));
		Map<String, String> configMap = configs.stream()
				.filter(c -> c.getMenuName() != null)
				.collect(Collectors.toMap(SystemConfig::getMenuName,
						c -> c.getValue() == null ? "" : c.getValue(), (a, b) -> b, LinkedHashMap::new));

		Enterprise ent = enterpriseMapper.selectOne(Wrappers.lambdaQuery(Enterprise.class)
				.eq(Enterprise::getId, 1L)
				.eq(Enterprise::getStatus, 1));

		SiteVO vo = new SiteVO();
		vo.setSiteRecordNumber(configMap.getOrDefault("site_record_number", ""));
		vo.setSiteAddress(buildAddress(ent));
		vo.setSiteTel(configMap.getOrDefault("site_tel", ""));
		vo.setSiteLogo(ent == null || ent.getLogo() == null ? "" : ent.getLogo());
		vo.setPasswordType(parsePasswordType(configMap.get("login_password_type")));
		vo.setPasswordLength(parseInteger(configMap.get("login_password_length"), 0));
		vo.setVersionName(APP_VERSION);
		vo.setGlobalAttachSize(parseInteger(configMap.get("global_attach_size"), 0));
		return vo;
	}

	private String buildAddress(Enterprise enterprise) {
		if (enterprise == null) {
			return "";
		}
		return String.valueOf(enterprise.getProvince() == null ? "" : enterprise.getProvince())
				+ (enterprise.getCity() == null ? "" : enterprise.getCity())
				+ (enterprise.getArea() == null ? "" : enterprise.getArea())
				+ (enterprise.getAddress() == null ? "" : enterprise.getAddress());
	}

	private String parsePasswordType(String raw) {
		if (!StringUtils.hasText(raw)) {
			return "02";
		}
		try {
			List<Integer> values = objectMapper.readValue(raw, new TypeReference<List<Integer>>() {
			});
			List<Integer> sorted = values.stream().filter(Objects::nonNull).sorted().toList();
			if (!sorted.isEmpty()) {
				return sorted.stream().map(String::valueOf).collect(Collectors.joining());
			}
		} catch (Exception ignored) {
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
			return StringUtils.hasText(raw) ? Integer.parseInt(raw) : defaultValue;
		} catch (NumberFormatException ex) {
			return defaultValue;
		}
	}

	private List<Long> parseIdList(String raw) {
		if (!StringUtils.hasText(raw)) {
			return List.of();
		}
		String value = raw.trim();
		try {
			if (value.startsWith("[") && value.endsWith("]")) {
				List<Long> parsed = objectMapper.readValue(value, new TypeReference<List<Long>>() {
				});
				return parsed.stream().filter(Objects::nonNull).toList();
			}
		} catch (Exception ignored) {
			// fallback
		}
		String normalized = value.replace("[", "").replace("]", "").replace("\"", "");
		if (!StringUtils.hasText(normalized)) {
			return List.of();
		}
		return java.util.Arrays.stream(normalized.split(","))
				.map(String::trim)
				.filter(StringUtils::hasText)
				.map(item -> {
					try {
						return Long.parseLong(item);
					} catch (NumberFormatException ex) {
						return null;
					}
				})
				.filter(Objects::nonNull)
				.toList();
	}

}
