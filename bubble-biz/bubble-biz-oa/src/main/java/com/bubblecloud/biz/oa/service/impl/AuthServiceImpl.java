package com.bubblecloud.biz.oa.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.bubblecloud.common.core.util.R;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.AdminMapper;
import com.bubblecloud.biz.oa.mapper.AssessScoreMapper;
import com.bubblecloud.biz.oa.mapper.EnterpriseMapper;
import com.bubblecloud.biz.oa.mapper.FrameAssistMapper;
import com.bubblecloud.biz.oa.mapper.RankJobMapper;
import com.bubblecloud.biz.oa.mapper.SystemConfigMapper;
import com.bubblecloud.biz.oa.constant.config.OaPhpJwtProperties;
import com.bubblecloud.biz.oa.util.OaPhpJwtTokenUtil;
import com.bubblecloud.biz.oa.service.AdminService;
import com.bubblecloud.biz.oa.service.AuthService;
import com.bubblecloud.biz.oa.service.SmsVerifyService;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.dto.FrameAssistView;
import com.bubblecloud.oa.api.dto.LoginDTO;
import com.bubblecloud.oa.api.dto.PhoneLoginDTO;
import com.bubblecloud.oa.api.dto.RegisterDTO;
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
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 兼容 PHP 的登录鉴权服务实现。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl extends UpServiceImpl<AdminMapper, Admin> implements AuthService {

	private static final int DEFAULT_ENTID = 1;

	private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

	private final AdminService adminService;

	private final OaPhpJwtTokenUtil tokenService;

	private final OaPhpJwtProperties jwtProperties;

	private final EnterpriseMapper enterpriseMapper;

	private final FrameAssistMapper frameAssistMapper;

	private final RankJobMapper rankJobMapper;

	private final AssessScoreMapper assessScoreMapper;

	private final SystemConfigMapper systemConfigMapper;

	private final ObjectMapper objectMapper;

	private final SmsVerifyService smsVerifyService;

	@Override
	public LoginVO login(LoginDTO dto) {
		Admin admin = adminService.getByAccount(dto.getAccount());
		if (ObjectUtil.isNull(admin)) {
			throw new IllegalArgumentException("账号不存在");
		}
		if (ObjectUtil.isNotNull(admin.getStatus()) && admin.getStatus() == 0) {
			throw new IllegalArgumentException("账号已被锁定");
		}
		String dbPassword = normalizePhpBcrypt(admin.getPassword());
		if (!ENCODER.matches(dto.getPassword(), dbPassword)) {
			throw new IllegalArgumentException("密码错误");
		}
		return buildLoginVo(admin);
	}

	@Override
	public LoginVO register(RegisterDTO dto) {
		if (!dto.getPassword().equals(dto.getPasswordConfirm())) {
			throw new IllegalArgumentException("两次密码不一致");
		}
		if (adminService.countByPhone(dto.getPhone()) > 0) {
			throw new IllegalArgumentException("该手机号已注册");
		}
		if (!smsVerifyService.verifyCode(dto.getPhone(), dto.getVerificationCode())) {
			throw new IllegalArgumentException("验证码错误或已过期");
		}
		Admin admin = adminService.createRegisteredUser(dto.getPhone(), ENCODER.encode(dto.getPassword()));
		return buildLoginVo(admin);
	}

	@Override
	public LoginVO phoneLogin(PhoneLoginDTO dto) {
		if (!smsVerifyService.verifyCode(dto.getPhone(), dto.getVerificationCode())) {
			throw new IllegalArgumentException("验证码错误或已过期");
		}
		Admin admin = adminService.ensureUserForPhoneLogin(dto.getPhone());
		if (ObjectUtil.isNotNull(admin.getStatus()) && admin.getStatus() == 0) {
			throw new IllegalArgumentException("账号已被锁定");
		}
		return buildLoginVo(admin);
	}

	@Override
	public LoginVO loginByPhone(String phone) {
		Admin admin = adminService.getByAccount(phone);
		if (ObjectUtil.isNull(admin)) {
			throw new IllegalArgumentException("账号不存在");
		}
		if (ObjectUtil.isNotNull(admin.getStatus()) && admin.getStatus() == 0) {
			throw new IllegalArgumentException("账号已被锁定");
		}
		return buildLoginVo(admin);
	}

	private LoginVO buildLoginVo(Admin admin) {
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
		if (ObjectUtil.isNull(admin)) {
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
		if (ObjectUtil.isNull(self)) {
			throw new IllegalArgumentException("用户不存在");
		}
		Admin byPhone = adminService.getByAccount(phone);
		if (ObjectUtil.isNull(byPhone)) {
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
		if (StrUtil.isBlank(raw)) {
			return Collections.emptyList();
		}
		try {
			return objectMapper.readValue(raw, new TypeReference<List<Object>>() {
			});
		}
		catch (Exception e) {
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
			String frameName = ObjectUtil.isNotNull(v.getFrameName()) ? v.getFrameName() : "";
			row.setFrame(new FrameNameRefVO(v.getFrameId(), frameName));
			frames.add(row);
		}
		return frames;
	}

	private RankJobLoginVO copyRankJob(Integer jobId) {
		if (ObjectUtil.isNull(jobId) || jobId == 0) {
			return null;
		}
		RankJob job = rankJobMapper.selectById(jobId.longValue());
		if (ObjectUtil.isNull(job)) {
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
		if (ObjectUtil.isNull(e)) {
			return vo;
		}
		vo.setTitle(nullToEmpty(e.getTitle()));
		vo.setEnterpriseName(nullToEmpty(e.getName()));
		vo.setEnterpriseNameEn(nullToEmpty(e.getEnterpriseNameEn()));
		vo.setEntid(e.getId());
		vo.setLogo(nullToEmpty(e.getLogo()));
		vo.setUniqued(nullToEmpty(e.getUniqued()));
		Integer max = assessScoreMapper.selectMaxScoreByEntid(DEFAULT_ENTID);
		vo.setMaxScore(ObjectUtil.isNotNull(max) ? max : 0);
		vo.setCulture(configValue("enterprise_culture", ""));
		vo.setComputeMode(parseIntConfig("assess_compute_mode", 1));
		return vo;
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
		SystemConfig c = systemConfigMapper
			.selectOne(Wrappers.lambdaQuery(SystemConfig.class).eq(SystemConfig::getConfigKey, key).last("LIMIT 1"));
		if (ObjectUtil.isNull(c) || ObjectUtil.isNull(c.getValue())) {
			return defaultVal;
		}
		return c.getValue();
	}

	private String nullToEmpty(String s) {
		return ObjectUtil.isNull(s) ? "" : s;
	}

	private String normalizePhpBcrypt(String hash) {
		if (ObjectUtil.isNull(hash)) {
			return "";
		}
		if (hash.startsWith("$2y$")) {
			return "$2a$" + hash.substring(4);
		}
		return hash;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(Admin req) {
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(Admin req) {
		return super.update(req);
	}

}
