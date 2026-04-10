package com.bubblecloud.oa.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户注册请求（对齐 PHP registerUser）。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@Data
public class RegisterDTO {

	@NotBlank(message = "手机号不能为空")
	@Pattern(regexp = "^1\\d{10}$", message = "请输入正确的手机号码")
	private String phone;

	@NotBlank(message = "密码不能为空")
	@Size(min = 5, message = "密码长度不正确")
	private String password;

	@NotBlank(message = "确认密码不能为空")
	private String passwordConfirm;

	@NotBlank(message = "短信验证码不能为空")
	private String verificationCode;

}
