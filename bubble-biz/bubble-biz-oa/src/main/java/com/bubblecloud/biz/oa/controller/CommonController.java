package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.biz.oa.security.OaCurrentUser;
import com.bubblecloud.biz.oa.service.AdminService;
import com.bubblecloud.biz.oa.service.MessageService;
import com.bubblecloud.biz.oa.service.SmsVerifyService;
import com.bubblecloud.biz.oa.service.SiteService;
import com.bubblecloud.biz.oa.service.SystemConfigService;
import com.bubblecloud.biz.oa.support.PhpResponse;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import cn.hutool.core.util.ObjectUtil;

/**
 * OA 公共接口。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/common")
@Tag(name = "公共接口")
public class CommonController {

	private final SystemConfigService systemConfigService;

	private final SiteService siteService;

	private final SmsVerifyService smsVerifyService;

	private final AdminService adminService;

	private final MessageService messageService;

	@GetMapping("/captcha")
	@Operation(summary = "验证码占位接口")
	public PhpResponse<CaptchaVO> captcha() {
		return PhpResponse.ok(new CaptchaVO("oa-captcha-key", ""));
	}

	@GetMapping("/config")
	@Operation(summary = "读取系统配置")
	public PhpResponse<ConfigVO> config(@RequestParam(defaultValue = "system") String type) {
		ConfigQueryDTO dto = new ConfigQueryDTO();
		dto.setType(type);
		return PhpResponse.ok(systemConfigService.config(dto));
	}

	@GetMapping("/site")
	@Operation(summary = "站点配置")
	public PhpResponse<SiteVO> site() {
		return PhpResponse.ok(siteService.site());
	}

	/**
	 * 商业授权占位（PHP common/auth，前端 entAuth 判断 status）。
	 */
	@GetMapping("/auth")
	@Operation(summary = "授权信息占位")
	public PhpResponse<CommonAuthVO> auth() {
		return PhpResponse.ok(new CommonAuthVO(1, 999));
	}

	@GetMapping("/verify/key")
	@Operation(summary = "获取短信发送 KEY")
	public PhpResponse<SmsVerifyKeyVO> verifyKey() {
		return PhpResponse.ok(smsVerifyService.createSendKey());
	}

	@PostMapping("/verify")
	@Operation(summary = "发送短信验证码")
	public PhpResponse<String> verify(@RequestBody @Valid SmsVerifySendDTO dto) {
		if (ObjectUtil.defaultIfNull(dto.getFrom(), 0) != 0) {
			long cnt = adminService.countByPhone(dto.getPhone());
			if (cnt > 0) {
				Admin a = adminService.getByAccount(dto.getPhone());
				if (ObjectUtil.isNotNull(a) && ObjectUtil.isNotNull(a.getStatus()) && a.getStatus() == 0) {
					return PhpResponse.failed("该手机号已被锁定");
				}
			}
			else {
				if (!systemConfigService.isRegistrationOpen()) {
					return PhpResponse.failed("短信发送失败，未注册的手机号");
				}
			}
		}
		try {
			smsVerifyService.sendVerifyCode(dto);
			return PhpResponse.ok("短信发送成功");
		}
		catch (IllegalArgumentException e) {
			return PhpResponse.failed(e.getMessage());
		}
	}

	@GetMapping("/version")
	@Operation(summary = "版本信息")
	public PhpResponse<CommonVersionVO> version() {
		return PhpResponse.ok(new CommonVersionVO("3.9.2", 48, "oa"));
	}

	@GetMapping("/message")
	@Operation(summary = "消息列表（工作台角标）")
	public PhpResponse<CommonMessageVO> message(Authentication authentication,
			@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "20") Integer limit,
			@RequestParam(required = false) String cate_id, @RequestParam(required = false) String title) {
		if (ObjectUtil.isNull(authentication) || !(authentication.getPrincipal() instanceof OaCurrentUser currentUser)) {
			return PhpResponse.failed("未登录");
		}
		Admin admin = adminService.getById(currentUser.getId());
		if (ObjectUtil.isNull(admin)) {
			return PhpResponse.failed("用户不存在");
		}
		return PhpResponse.ok(messageService.getMessageList(currentUser.getId(), admin.getUid(), 1, page, limit,
				ObjectUtil.isNull(cate_id) ? "" : cate_id, ObjectUtil.isNull(title) ? "" : title));
	}

	@PutMapping("/message/{id}/{isRead}")
	@Operation(summary = "修改消息已读状态")
	public PhpResponse<String> updateMessage(Authentication authentication, @PathVariable long id,
			@PathVariable int isRead) {
		if (ObjectUtil.isNull(authentication) || !(authentication.getPrincipal() instanceof OaCurrentUser currentUser)) {
			return PhpResponse.failed("未登录");
		}
		Admin admin = adminService.getById(currentUser.getId());
		if (ObjectUtil.isNull(admin)) {
			return PhpResponse.failed("用户不存在");
		}
		try {
			messageService.updateMessageRead(currentUser.getId(), admin.getUid(), id, isRead);
			return PhpResponse.ok("common.update.succ");
		}
		catch (IllegalArgumentException e) {
			return PhpResponse.failed(e.getMessage());
		}
	}

}
