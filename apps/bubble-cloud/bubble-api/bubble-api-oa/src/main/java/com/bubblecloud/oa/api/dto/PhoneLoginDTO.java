package com.bubblecloud.oa.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 短信验证码登录（对齐 PHP phone_login）。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@Data
public class PhoneLoginDTO {

	@NotBlank(message = "手机号不能为空")
	@Pattern(regexp = "^1\\d{10}$", message = "请输入正确的手机号码")
	private String phone;

	@NotBlank(message = "短信验证码不能为空")
	private String verificationCode;

}
