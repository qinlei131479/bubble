package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.biz.oa.security.OaCurrentUser;
import com.bubblecloud.biz.oa.service.AuthService;
import com.bubblecloud.biz.oa.service.ScanLoginService;
import com.bubblecloud.biz.oa.support.PhpResponse;
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

	private final AuthService authService;

	private final ScanLoginService scanLoginService;

	@PostMapping("/login")
	@Operation(summary = "账号密码登录")
	public PhpResponse<LoginVO> login(@RequestBody @Valid LoginDTO dto) {
		return PhpResponse.ok(authService.login(dto));
	}

	@GetMapping("/info")
	@Operation(summary = "当前登录用户信息（PHP：userInfo + enterprise）")
	public PhpResponse<LoginInfoVO> info(Authentication authentication) {
		if (ObjectUtil.isNull(authentication) || !(authentication.getPrincipal() instanceof OaCurrentUser currentUser)) {
			return PhpResponse.failed("未登录");
		}
		LoginInfoVO data = authService.loginInfo(currentUser.getId());
		return ObjectUtil.isNull(data) ? PhpResponse.failed("用户不存在") : PhpResponse.ok(data);
	}

	@GetMapping("/logout")
	@Operation(summary = "退出登录")
	public PhpResponse<Boolean> logout() {
		return PhpResponse.ok(Boolean.TRUE);
	}

	@PutMapping({ "/savePassword", "/common/savePassword" })
	@Operation(summary = "修改密码（与 PHP ent/user/savePassword 一致）")
	public PhpResponse<String> savePassword(Authentication authentication, @RequestBody @Valid SavePasswordDTO dto) {
		if (ObjectUtil.isNull(authentication) || !(authentication.getPrincipal() instanceof OaCurrentUser currentUser)) {
			return PhpResponse.failed("未登录");
		}
		authService.updatePassword(currentUser.getId(), dto.getPhone(), dto.getPassword());
		return PhpResponse.ok("ok");
	}

	@PostMapping("/register")
	@Operation(summary = "用户注册（短信验证码 + 密码）")
	public PhpResponse<LoginVO> register(@RequestBody @Valid RegisterDTO dto) {
		return PhpResponse.ok(authService.register(dto));
	}

	@PostMapping("/phone_login")
	@Operation(summary = "短信验证码登录（无账号则自动注册）")
	public PhpResponse<LoginVO> phoneLogin(@RequestBody @Valid PhoneLoginDTO dto) {
		return PhpResponse.ok(authService.phoneLogin(dto));
	}

	@GetMapping("/scan_key")
	@Operation(summary = "获取扫码登录 key")
	public PhpResponse<ScanKeyVO> scanKey() {
		return PhpResponse.ok(scanLoginService.createScanKey());
	}

	@PostMapping("/scan_status")
	@Operation(summary = "轮询扫码状态")
	public PhpResponse<ScanStatusResultVO> scanStatus(@RequestBody @Valid ScanStatusDTO dto) {
		return PhpResponse.ok(scanLoginService.pollStatus(dto.getKey()));
	}

}
