package com.bubblecloud.oa.api.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * POST /ent/user/checkpwd。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CheckPwdDTO {

	@NotBlank
	@Size(min = 5, message = "密码长度不正确")
	private String password;

	@NotBlank
	@Size(min = 5, message = "密码长度不正确")
	private String passwordConfirm;

}
