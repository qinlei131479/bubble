package com.bubblecloud.biz.oa.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.AssessScoreMapper;
import com.bubblecloud.biz.oa.mapper.EnterpriseMapper;
import com.bubblecloud.biz.oa.mapper.FrameAssistMapper;
import com.bubblecloud.biz.oa.mapper.RankJobMapper;
import com.bubblecloud.biz.oa.mapper.SystemConfigMapper;
import com.bubblecloud.biz.oa.security.OaPhpJwtProperties;
import com.bubblecloud.biz.oa.security.OaPhpJwtTokenService;
import com.bubblecloud.biz.oa.service.AdminService;
import com.bubblecloud.biz.oa.service.AuthService;
import com.bubblecloud.oa.api.dto.FrameAssistView;
import com.bubblecloud.oa.api.dto.LoginDTO;
import com.bubblecloud.oa.api.entity.Admin;
import com.bubblecloud.oa.api.entity.Enterprise;
import com.bubblecloud.oa.api.entity.RankJob;
import com.bubblecloud.oa.api.entity.SystemConfig;
import com.bubblecloud.oa.api.vo.LoginInfoVO;
import com.bubblecloud.oa.api.vo.LoginVO;
import com.bubblecloud.oa.api.vo.auth.EnterpriseLoginVO;
import com.bubblecloud.oa.api.vo.auth.FrameAssistLoginRowVO;
import com.bubblecloud.oa.api.vo.auth.FrameNameRefVO;
import com.bubblecloud.oa.api.vo.auth.LoginUserInfoPayloadVO;
import com.bubblecloud.oa.api.vo.auth.RankJobLoginVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 兼容 PHP 的登录鉴权服务实现。
 *
 * @author qinlei
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

	private LoginUserInfoPayloadVO buildUserInfo(Admin admin) {
		LoginUserInfoPayloadVO vo = new LoginUserInfoPayloadVO();
		vo.setId(admin.getId());
		vo.setUid(admin.getUid());
		vo.setAccount(admin.getAccount());
		vo.setAvatar(admin.getAvatar());
		vo.setName(admin.getName());
		vo.setPhone(admin.getPhone());
		vo.setIsAdmin(admin.getIsAdmin());
		vo.setUniOnline(admin.getUniOnline());
		vo.setClientId(admin.getClientId());
		vo.setScanKey(admin.getScanKey());
		vo.setLastIp(admin.getLastIp());
		vo.setLoginCount(admin.getLoginCount());
		vo.setStatus(admin.getStatus());
		vo.setIsInit(admin.getIsInit());
		vo.setLanguage(admin.getLanguage());
		vo.setMark(admin.getMark());
		vo.setCreatedAt(admin.getCreatedAt());
		vo.setUpdatedAt(admin.getUpdatedAt());
		vo.setRoles(parseRoles(admin.getRoles()));
		vo.setFrames(buildFrameRows(admin.getId()));
		vo.setJob(copyRankJob(admin.getJob()));
		vo.setRealName(admin.getName());
		return vo;
	}

	private List<Object> parseRoles(String raw) {
		if (!StringUtils.hasText(raw)) {
			return Collections.emptyList();
		}
		try {
			return objectMapper.readValue(raw, new TypeReference<List<Object>>() {
			});
		} catch (Exception e) {
			return Collections.emptyList();
		}
	}

	private List<FrameAssistLoginRowVO> buildFrameRows(Long userId) {
		List<FrameAssistView> rows = frameAssistMapper.selectUserFrames(userId, DEFAULT_ENTID);
		List<FrameAssistLoginRowVO> frames = new ArrayList<>();
		for (FrameAssistView v : rows) {
			FrameAssistLoginRowVO row = new FrameAssistLoginRowVO();
			row.setId(v.getId());
			row.setEntid(v.getEntid());
			row.setFrameId(v.getFrameId());
			row.setUserId(v.getUserId());
			row.setIsMastart(v.getIsMastart());
			row.setIsAdmin(v.getIsAdmin());
			row.setSuperiorUid(v.getSuperiorUid());
			String frameName = v.getFrameName() != null ? v.getFrameName() : "";
			row.setFrame(new FrameNameRefVO(v.getFrameId(), frameName));
			frames.add(row);
		}
		return frames;
	}

	private RankJobLoginVO copyRankJob(Integer jobId) {
		if (jobId == null || jobId == 0) {
			return null;
		}
		RankJob job = rankJobMapper.selectById(jobId.longValue());
		if (job == null) {
			return null;
		}
		RankJobLoginVO vo = new RankJobLoginVO();
		vo.setId(job.getId());
		vo.setEntid(job.getEntid());
		vo.setName(job.getName());
		vo.setDescribe(job.getDescribe());
		vo.setDuty(job.getDuty());
		vo.setStatus(job.getStatus());
		vo.setCreatedAt(job.getCreatedAt());
		vo.setUpdatedAt(job.getUpdatedAt());
		return vo;
	}

	private EnterpriseLoginVO buildEnterprise() {
		Enterprise e = enterpriseMapper.selectById((long) DEFAULT_ENTID);
		EnterpriseLoginVO vo = new EnterpriseLoginVO();
		if (e == null) {
			return vo;
		}
		vo.setTitle(nullToEmpty(e.getTitle()));
		vo.setEnterpriseName(nullToEmpty(e.getName()));
		vo.setEnterpriseNameEn(nullToEmpty(e.getEnterpriseNameEn()));
		vo.setEntid(e.getId());
		vo.setLogo(nullToEmpty(e.getLogo()));
		vo.setUniqued(nullToEmpty(e.getUniqued()));
		Integer max = assessScoreMapper.selectMaxScoreByEntid(DEFAULT_ENTID);
		vo.setMaxScore(max != null ? max : 0);
		vo.setCulture(configValue("enterprise_culture", ""));
		vo.setComputeMode(parseIntConfig("assess_compute_mode", 1));
		return vo;
	}

	private int parseIntConfig(String key, int def) {
		String v = configValue(key, String.valueOf(def));
		try {
			return Integer.parseInt(v.trim());
		} catch (NumberFormatException e) {
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
