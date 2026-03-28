package com.bubblecloud.biz.oa.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.dto.FrameAssistView;
import com.bubblecloud.biz.oa.mapper.AssessScoreMapper;
import com.bubblecloud.biz.oa.mapper.EnterpriseMapper;
import com.bubblecloud.biz.oa.mapper.FrameAssistMapper;
import com.bubblecloud.biz.oa.mapper.RankJobMapper;
import com.bubblecloud.biz.oa.mapper.SystemConfigMapper;
import com.bubblecloud.biz.oa.security.OaPhpJwtProperties;
import com.bubblecloud.biz.oa.security.OaPhpJwtTokenService;
import com.bubblecloud.biz.oa.service.AdminService;
import com.bubblecloud.biz.oa.service.AuthService;
import com.bubblecloud.oa.api.dto.LoginDTO;
import com.bubblecloud.oa.api.entity.Admin;
import com.bubblecloud.oa.api.entity.Enterprise;
import com.bubblecloud.oa.api.entity.RankJob;
import com.bubblecloud.oa.api.entity.SystemConfig;
import com.bubblecloud.oa.api.vo.LoginInfoVO;
import com.bubblecloud.oa.api.vo.LoginVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 兼容 PHP 的登录鉴权服务实现。
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private static final int DEFAULT_ENTID = 1;

	private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

	private final AdminService adminService;

	private final OaPhpJwtTokenService tokenService;

	private final OaPhpJwtProperties jwtProperties;

	private final EnterpriseMapper enterpriseMapper;

	private final FrameAssistMapper frameAssistMapper;

	private final RankJobMapper rankJobMapper;

	private final AssessScoreMapper assessScoreMapper;

	private final SystemConfigMapper systemConfigMapper;

	private final ObjectMapper objectMapper;

	@Override
	public LoginVO login(LoginDTO dto) {
		Admin admin = adminService.getByAccount(dto.getAccount());
		if (admin == null) {
			throw new IllegalArgumentException("账号不存在");
		}
		if (admin.getStatus() != null && admin.getStatus() == 0) {
			throw new IllegalArgumentException("账号已被锁定");
		}
		String dbPassword = normalizePhpBcrypt(admin.getPassword());
		if (!ENCODER.matches(dto.getPassword(), dbPassword)) {
			throw new IllegalArgumentException("密码错误");
		}

		LoginVO vo = new LoginVO();
		vo.setId(admin.getId());
		vo.setAccount(admin.getAccount());
		vo.setName(admin.getName());
		vo.setAvatar(admin.getAvatar());
		vo.setExpiresIn(jwtProperties.getExpireSeconds().longValue());
		vo.setToken(tokenService.createToken(admin.getId(), admin.getAccount()));
		return vo;
	}

	@Override
	public LoginInfoVO loginInfo(Long userId) {
		Admin admin = adminService.getById(userId);
		if (admin == null) {
			return null;
		}
		LoginInfoVO vo = new LoginInfoVO();
		vo.setUserInfo(buildUserInfo(admin));
		vo.setEnterprise(buildEnterprise());
		return vo;
	}

	@Override
	public void updatePassword(Long userId, String phone, String newPassword) {
		Admin self = adminService.getById(userId);
		if (self == null) {
			throw new IllegalArgumentException("用户不存在");
		}
		Admin byPhone = adminService.getByAccount(phone);
		if (byPhone == null) {
			throw new IllegalArgumentException("您的手机号码尚未注册");
		}
		if (!self.getId().equals(byPhone.getId())) {
			throw new IllegalArgumentException("无法修改其他用户密码");
		}
		self.setPassword(ENCODER.encode(newPassword));
		self.setIsInit(0);
		adminService.updateById(self);
	}

	private Map<String, Object> buildUserInfo(Admin admin) {
		Map<String, Object> map = adminToSnakeMap(admin);
		map.put("roles", parseRoles(admin.getRoles()));
		map.put("frames", buildFrames(admin.getId()));
		map.put("job", jobMap(admin.getJob()));
		map.put("real_name", admin.getName());
		return map;
	}

	private List<Object> parseRoles(String raw) {
		if (!StringUtils.hasText(raw)) {
			return Collections.emptyList();
		}
		try {
			return objectMapper.readValue(raw, new TypeReference<List<Object>>() {});
		}
		catch (Exception e) {
			return Collections.emptyList();
		}
	}

	private List<Map<String, Object>> buildFrames(Long userId) {
		List<FrameAssistView> rows = frameAssistMapper.selectUserFrames(userId, DEFAULT_ENTID);
		List<Map<String, Object>> frames = new ArrayList<>();
		for (FrameAssistView v : rows) {
			Map<String, Object> row = new LinkedHashMap<>();
			row.put("id", v.getId());
			row.put("entid", v.getEntid());
			row.put("frame_id", v.getFrameId());
			row.put("user_id", v.getUserId());
			row.put("is_mastart", v.getIsMastart());
			row.put("is_admin", v.getIsAdmin());
			row.put("superior_uid", v.getSuperiorUid());
			Map<String, Object> frame = new LinkedHashMap<>();
			frame.put("id", v.getFrameId());
			frame.put("name", v.getFrameName() != null ? v.getFrameName() : "");
			row.put("frame", frame);
			frames.add(row);
		}
		return frames;
	}

	private Object jobMap(Integer jobId) {
		if (jobId == null || jobId == 0) {
			return null;
		}
		RankJob job = rankJobMapper.selectById(jobId.longValue());
		if (job == null) {
			return null;
		}
		return jobToSnakeMap(job);
	}

	private Map<String, Object> buildEnterprise() {
		Enterprise e = enterpriseMapper.selectById((long) DEFAULT_ENTID);
		Map<String, Object> map = new LinkedHashMap<>();
		if (e == null) {
			return map;
		}
		map.put("title", nullToEmpty(e.getTitle()));
		map.put("enterprise_name", nullToEmpty(e.getName()));
		map.put("enterprise_name_en", nullToEmpty(e.getEnterpriseNameEn()));
		map.put("entid", e.getId());
		map.put("logo", nullToEmpty(e.getLogo()));
		map.put("uniqued", nullToEmpty(e.getUniqued()));
		Integer max = assessScoreMapper.selectMaxScoreByEntid(DEFAULT_ENTID);
		map.put("maxScore", max != null ? max : 0);
		map.put("culture", configValue("enterprise_culture", ""));
		map.put("compute_mode", parseIntConfig("assess_compute_mode", 1));
		return map;
	}

	private int parseIntConfig(String key, int def) {
		String v = configValue(key, String.valueOf(def));
		try {
			return Integer.parseInt(v.trim());
		}
		catch (NumberFormatException e) {
			return def;
		}
	}

	private String configValue(String key, String defaultVal) {
		SystemConfig c = systemConfigMapper.selectOne(Wrappers.lambdaQuery(SystemConfig.class)
			.eq(SystemConfig::getMenuName, key)
			.last("LIMIT 1"));
		if (c == null || c.getValue() == null) {
			return defaultVal;
		}
		return c.getValue();
	}

	private String nullToEmpty(String s) {
		return s == null ? "" : s;
	}

	private Map<String, Object> adminToSnakeMap(Admin admin) {
		ObjectMapper m = objectMapper.copy();
		m.registerModule(new JavaTimeModule());
		m.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		m.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
		@SuppressWarnings("unchecked")
		Map<String, Object> map = m.convertValue(admin, Map.class);
		map.remove("password");
		map.remove("deleted_at");
		return map;
	}

	private Map<String, Object> jobToSnakeMap(RankJob job) {
		ObjectMapper m = objectMapper.copy();
		m.registerModule(new JavaTimeModule());
		m.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		m.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
		@SuppressWarnings("unchecked")
		Map<String, Object> map = m.convertValue(job, Map.class);
		return map;
	}

	private String normalizePhpBcrypt(String hash) {
		if (hash == null) {
			return "";
		}
		if (hash.startsWith("$2y$")) {
			return "$2a$" + hash.substring(4);
		}
		return hash;
	}

}
