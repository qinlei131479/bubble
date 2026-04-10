package com.bubblecloud.oa.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * POST /ent/common/verify 发送短信验证码。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@Data
public class SmsVerifySendDTO {

	@NotBlank
	@Pattern(regexp = "^1\\d{10}$", message = "请输入正确的手机号码")
	private String phone;

	@NotBlank(message = "请先获取短信发送KEY")
	private String key;

	private Integer types;

	/** 来源：0 默认；非 0 时未注册手机号需系统开启注册 */
	private Integer from;

}
