package com.bubblecloud.api.backend.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 前端展示令牌管理
 *
 * @author lengleng
 * @date 2022/6/2
 */
@Data
@Schema(description = "用户token")
public class TokenVO {

	@Schema(description = "主键id")
	private String id;

	@Schema(description = "用户id")
	private Long userId;

	@Schema(description = "终端id")
	private String clientId;

	@Schema(description = "用户名称")
	private String username;

	@Schema(description = "访问token")
	private String accessToken;

	@Schema(description = "使用时间")
	private String issuedAt;

	@Schema(description = "过期时间")
	private String expiresAt;

}
