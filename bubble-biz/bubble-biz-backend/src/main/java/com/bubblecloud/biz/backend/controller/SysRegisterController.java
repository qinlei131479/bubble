package com.bubblecloud.biz.backend.controller;

import com.bubblecloud.backend.api.dto.RegisterUserDTO;
import com.bubblecloud.biz.backend.service.SysUserService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.log.annotation.SysLog;
import com.bubblecloud.common.security.annotation.Inner;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户注册控制器：提供用户注册功能
 *
 * @author lengleng
 * @date 2025/05/30
 */
@RestController
@RequestMapping("/register")
@RequiredArgsConstructor
@Tag(description = "register", name = "注册用户管理模块")
@ConditionalOnProperty(name = "register.user", matchIfMissing = true)
public class SysRegisterController {

	private final SysUserService userService;

	/**
	 * 注册用户
	 * @param registerUserDTO 注册用户信息DTO
	 * @return 注册结果封装对象
	 */
	@Inner(value = false)
	@SysLog("注册用户")
	@PostMapping("/user")
	@Operation(summary = "注册用户", description = "注册用户")
	public R<Boolean> registerUser(@RequestBody RegisterUserDTO registerUserDTO) {
		return userService.registerUser(registerUserDTO);
	}

}
