package com.bubblecloud.oa.api.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import jakarta.validation.constraints.Email;
import lombok.Data;

/**
 * PUT /ent/user/userInfo 修改当前用户（对齐 PHP UserController::update）。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserSelfUpdateDTO {

	private String avatar;

	private String name;

	@Email(message = "请输入正确的email地址")
	private String email;

	private String phone;

	private String password;

	private String passwordConfirm;

	private String verificationCode;

}
