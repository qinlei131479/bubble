package com.bubblecloud.oa.api.vo;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 验证码响应（兼容 PHP 前端蛇形字段）。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "验证码")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CaptchaVO {

	@Schema(description = "验证码缓存键")
	private String key;

	@Schema(description = "验证码图片 Base64")
	private String img;

}
