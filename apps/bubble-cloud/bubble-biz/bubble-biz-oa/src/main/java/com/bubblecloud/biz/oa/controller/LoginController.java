package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.biz.oa.constant.config.OaCurrentUser;
import com.bubblecloud.biz.oa.service.LoginService;
import com.bubblecloud.biz.oa.util.OaSecurityUtil;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.dto.LoginDTO;
import com.bubblecloud.oa.api.dto.PhoneLoginDTO;
import com.bubblecloud.oa.api.dto.RegisterDTO;
import com.bubblecloud.oa.api.dto.SavePasswordDTO;
import com.bubblecloud.oa.api.dto.ScanStatusDTO;
import com.bubblecloud.oa.api.vo.LoginInfoVO;
import com.bubblecloud.oa.api.vo.LoginVO;
import com.bubblecloud.oa.api.vo.ScanKeyVO;
import com.bubblecloud.oa.api.vo.ScanStatusResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cn.hutool.core.util.ObjectUtil;

/**
 * OA 登录鉴权接口（兼容 PHP 路径）。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/user")
@Tag(name = "登录鉴权")
public class LoginController {

	private final LoginService loginService;

	@PostMapping("/login")
	@Operation(summary = "账号密码登录")
	public R<LoginVO> login(@RequestBody @Valid LoginDTO dto) {
		return R.phpOk(loginService.login(dto));
	}

	@GetMapping("/info")
	@Operation(summary = "当前登录用户信息（PHP：userInfo + enterprise）")
	public R<LoginInfoVO> info(Authentication authentication) {
		if (ObjectUtil.isNull(authentication)
				|| !(authentication.getPrincipal() instanceof OaCurrentUser currentUser)) {
			return R.phpFailed("未登录");
		}
		LoginInfoVO data = loginService.loginInfo(OaSecurityUtil.currentUserId());
		return ObjectUtil.isNull(data) ? R.phpFailed("用户不存在") : R.phpOk(data);
	}

	@GetMapping("/logout")
	@Operation(summary = "退出登录")
	public R<Boolean> logout() {
		return R.phpOk(Boolean.TRUE);
	}

	@PutMapping({ "/savePassword", "/common/savePassword" })
	@Operation(summary = "修改密码（与 PHP ent/user/savePassword 一致）")
	public R<String> savePassword(@RequestBody @Valid SavePasswordDTO dto) {
		loginService.updatePassword(OaSecurityUtil.currentUserId(), dto.getPhone(), dto.getPassword());
		return R.phpOk("ok");
	}

	@PostMapping("/register")
	@Operation(summary = "用户注册（短信验证码 + 密码）")
	public R<LoginVO> register(@RequestBody @Valid RegisterDTO dto) {
		return R.phpOk(loginService.register(dto));
	}

	@PostMapping("/phone_login")
	@Operation(summary = "短信验证码登录（无账号则自动注册）")
	public R<LoginVO> phoneLogin(@RequestBody @Valid PhoneLoginDTO dto) {
		return R.phpOk(loginService.phoneLogin(dto));
	}

	@GetMapping("/scan_key")
	@Operation(summary = "获取扫码登录 key")
	public R<ScanKeyVO> scanKey() {
		return R.phpOk(loginService.createScanKey());
	}

	@PostMapping("/scan_status")
	@Operation(summary = "轮询扫码状态")
	public R<ScanStatusResultVO> scanStatus(@RequestBody @Valid ScanStatusDTO dto) {
		return R.phpOk(loginService.pollStatus(dto.getKey()));
	}

}
