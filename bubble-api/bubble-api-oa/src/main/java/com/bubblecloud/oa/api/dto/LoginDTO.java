package com.bubblecloud.oa.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登录请求参数。
 *
 * @author qinlei
 */
@Data
@Schema(description = "登录请求")
public class LoginDTO {

	@NotBlank(message = "账号不能为空")
	@Schema(description = "账号")
	private String account;

	@NotBlank(message = "密码不能为空")
	@Schema(description = "密码")
	private String password;

	@Schema(description = "语言")
	private String language;

}
