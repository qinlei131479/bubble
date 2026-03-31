package com.bubblecloud.oa.api.vo;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 登录响应（兼容 PHP 前端蛇形字段命名）。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@Data
@Schema(description = "登录响应")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class LoginVO {

	@Schema(description = "JWT token")
	private String token;

	@Schema(description = "token 类型")
	private String tokenType = "Bearer";

	@Schema(description = "过期秒数")
	private Long expiresIn;

	@Schema(description = "用户ID")
	private Long id;

	@Schema(description = "账号")
	private String account;

	@Schema(description = "姓名")
	private String name;

	@Schema(description = "头像")
	private String avatar;

}
