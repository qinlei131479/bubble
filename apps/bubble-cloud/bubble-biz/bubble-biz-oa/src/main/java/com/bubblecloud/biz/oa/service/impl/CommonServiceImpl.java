package com.bubblecloud.biz.oa.service.impl;

import com.bubblecloud.biz.oa.service.*;
import com.bubblecloud.biz.oa.util.OaSecurityUtil;
import com.bubblecloud.oa.api.dto.ConfigQueryDTO;
import com.bubblecloud.oa.api.dto.SmsVerifySendDTO;
import com.bubblecloud.oa.api.entity.Admin;
import com.bubblecloud.oa.api.vo.CaptchaVO;
import com.bubblecloud.oa.api.vo.ConfigVO;
import com.bubblecloud.oa.api.vo.SiteVO;
import com.bubblecloud.oa.api.vo.SmsVerifyKeyVO;
import com.bubblecloud.oa.api.vo.common.CityTreeNodeVO;
import com.bubblecloud.oa.api.vo.common.CommonAuthVO;
import com.bubblecloud.oa.api.vo.common.CommonMessageVO;
import com.bubblecloud.oa.api.vo.common.CommonSiteAddressVO;
import com.bubblecloud.oa.api.vo.common.CommonVersionVO;
import com.bubblecloud.oa.api.vo.common.InitDataUrlVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import java.util.List;

/**
 * {@link CommonService} 实现。
 *
 * @author qinlei
 * @date 2026/3/31
 */
@Service
@RequiredArgsConstructor
public class CommonServiceImpl implements CommonService {

	private final SystemConfigService systemConfigService;

	private final SiteService siteService;

	private final AdminService adminService;

	private final EnterpriseMessageNoticeService enterpriseMessageNoticeService;

	private final SystemBackupService systemBackupService;

	private final SystemCityService systemCityService;

	private final OaImageCaptchaService oaImageCaptchaService;

	@Override
	public CaptchaVO captcha() {
		return oaImageCaptchaService.create();
	}

	@Override
	public ConfigVO config(ConfigQueryDTO dto) {
		return systemConfigService.config(dto);
	}

	@Override
	public SiteVO site() {
		return siteService.site();
	}

	@Override
	public CommonAuthVO auth() {
		// 与 PHP success 空 data 一致：前端仅 status===-1 时弹授权；0 表示不提醒
		return new CommonAuthVO(0, 0);
	}

	@Override
	public SmsVerifyKeyVO verifyKey() {
		throw new IllegalArgumentException("短信验证功能本阶段未开放");
	}

	@Override
	public void sendVerifySms(SmsVerifySendDTO dto) {
		throw new IllegalArgumentException("短信验证功能本阶段未开放");
	}

	@Override
	public CommonMessageVO messageList(Integer page, Integer limit, String cateId, String title) {
		Long userId = OaSecurityUtil.currentUserId();
		Admin admin = requireAdmin(userId);
		return enterpriseMessageNoticeService.getMessageList(userId, admin.getUid(), 1L, page, limit,
				ObjectUtil.isNull(cateId) ? "" : cateId, ObjectUtil.isNull(title) ? "" : title, 0);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateMessageRead(Long messageId, Integer isRead) {
		Long userId = OaSecurityUtil.currentUserId();
		Admin admin = requireAdmin(userId);
		enterpriseMessageNoticeService.updateMessageRead(userId, admin.getUid(), messageId, isRead);
	}

	@Override
	public CommonVersionVO version() {
		return new CommonVersionVO("3.9.2", 48, "oa");
	}

	@Override
	public CommonSiteAddressVO siteAddress() {
		String address = systemConfigService.getConfigRawValue("site_url");
		String aiImage = systemConfigService.getConfigRawValue("ai_image");
		String logo = systemConfigService.getConfigRawValue("ent_website_logo");
		String siteName = systemConfigService.getConfigRawValue("site_name");
		return new CommonSiteAddressVO(StrUtil.nullToEmpty(address), StrUtil.nullToEmpty(aiImage),
				StrUtil.nullToEmpty(logo), StrUtil.nullToEmpty(siteName));
	}

	@Override
	public InitDataUrlVO initData(String version) {
		String path = systemBackupService.findLatestPathByVersion(StrUtil.nullToEmpty(version));
		if (StrUtil.isBlank(path)) {
			return new InitDataUrlVO("");
		}
		String base = StrUtil.removeSuffix(systemConfigService.getConfigRawValue("site_url"), "/");
		if (StrUtil.isBlank(base)) {
			return new InitDataUrlVO("");
		}
		String p = path.startsWith("/") ? path : "/" + path;
		return new InitDataUrlVO(base + p);
	}

	@Override
	public List<CityTreeNodeVO> cityTree() {
		return systemCityService.cityTree();
	}

	@Override
	public void logoutSession() {
		SecurityContextHolder.clearContext();
	}

	private Admin requireAdmin(long adminId) {
		Admin admin = adminService.getById(adminId);
		if (ObjectUtil.isNull(admin)) {
			throw new IllegalArgumentException("用户不存在");
		}
		return admin;
	}

}
