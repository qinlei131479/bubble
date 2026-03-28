package com.bubblecloud.biz.oa.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * PHP JWT 配置。
 */
@Data
@ConfigurationProperties(prefix = "oa.php-jwt")
public class OaPhpJwtProperties {

	/**
	 * 与 PHP 侧保持一致的 JWT_SECRET。
	 */
	private String secret = "";

	/**
	 * 默认过期时间（秒）。
	 */
	private Long expireSeconds = 7200L;

	/**
	 * 签发者。
	 */
	private String issuer = "tuoluojiang-oa-java";

}
