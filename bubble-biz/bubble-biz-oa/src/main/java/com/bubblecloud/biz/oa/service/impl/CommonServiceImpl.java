package com.bubblecloud.biz.oa.service.impl;

import com.bubblecloud.biz.oa.security.OaCurrentUser;
import com.bubblecloud.biz.oa.service.AdminService;
import com.bubblecloud.biz.oa.service.CommonService;
import com.bubblecloud.biz.oa.service.MessageService;
import com.bubblecloud.biz.oa.service.SiteService;
import com.bubblecloud.biz.oa.service.SmsVerifyService;
import com.bubblecloud.biz.oa.service.SystemConfigService;
import com.bubblecloud.oa.api.dto.ConfigQueryDTO;
import com.bubblecloud.oa.api.dto.SmsVerifySendDTO;
import com.bubblecloud.oa.api.entity.Admin;
import com.bubblecloud.oa.api.vo.CaptchaVO;
import com.bubblecloud.oa.api.vo.ConfigVO;
import com.bubblecloud.oa.api.vo.SiteVO;
import com.bubblecloud.oa.api.vo.SmsVerifyKeyVO;
import com.bubblecloud.oa.api.vo.common.CommonAuthVO;
import com.bubblecloud.oa.api.vo.common.CommonMessageVO;
import com.bubblecloud.oa.api.vo.common.CommonVersionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.util.ObjectUtil;

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

	private final SmsVerifyService smsVerifyService;

	private final AdminService adminService;

	private final MessageService messageService;

	@Override
	public CaptchaVO captcha() {
		return new CaptchaVO("oa-captcha-key", "");
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
		return new CommonAuthVO(1, 999);
	}

	@Override
	public SmsVerifyKeyVO verifyKey() {
		return smsVerifyService.createSendKey();
	}

	@Override
	public void sendVerifySms(SmsVerifySendDTO dto) {
		if (ObjectUtil.defaultIfNull(dto.getFrom(), 0) != 0) {
			long cnt = adminService.countByPhone(dto.getPhone());
			if (cnt > 0) {
				Admin a = adminService.getByAccount(dto.getPhone());
				if (ObjectUtil.isNotNull(a) && ObjectUtil.isNotNull(a.getStatus()) && a.getStatus() == 0) {
					throw new IllegalArgumentException("该手机号已被锁定");
				}
			}
			else {
				if (!systemConfigService.isRegistrationOpen()) {
					throw new IllegalArgumentException("短信发送失败，未注册的手机号");
				}
			}
		}
		smsVerifyService.sendVerifyCode(dto);
	}

	@Override
	public CommonMessageVO messageList(Authentication authentication, Integer page, Integer limit, String cateId,
			String title) {
		OaCurrentUser currentUser = requireOaUser(authentication);
		Admin admin = requireAdmin(currentUser.getId());
		return messageService.getMessageList(currentUser.getId(), admin.getUid(), 1, page, limit,
				ObjectUtil.isNull(cateId) ? "" : cateId, ObjectUtil.isNull(title) ? "" : title);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateMessageRead(Authentication authentication, long messageId, int isRead) {
		OaCurrentUser currentUser = requireOaUser(authentication);
		Admin admin = requireAdmin(currentUser.getId());
		messageService.updateMessageRead(currentUser.getId(), admin.getUid(), messageId, isRead);
	}

	@Override
	public CommonVersionVO version() {
		return new CommonVersionVO("3.9.2", 48, "oa");
	}

	private static OaCurrentUser requireOaUser(Authentication authentication) {
		if (ObjectUtil.isNull(authentication) || !(authentication.getPrincipal() instanceof OaCurrentUser u)) {
			throw new IllegalArgumentException("未登录");
		}
		return u;
	}

	private Admin requireAdmin(long adminId) {
		Admin admin = adminService.getById(adminId);
		if (ObjectUtil.isNull(admin)) {
			throw new IllegalArgumentException("用户不存在");
		}
		return admin;
	}

}
