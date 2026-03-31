package com.bubblecloud.biz.oa.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 兼容 PHP 的 HS256 JWT 令牌服务。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@Component
@RequiredArgsConstructor
public class OaPhpJwtTokenService {

	private static final String HMAC_SHA256 = "HmacSHA256";

	private final OaPhpJwtProperties properties;

	private final ObjectMapper objectMapper;

	public String createToken(Long userId, String account) {
		try {
			long now = Instant.now().getEpochSecond();
			long exp = now + properties.getExpireSeconds();

			Map<String, Object> header = new HashMap<>();
			header.put("alg", "HS256");
			header.put("typ", "JWT");

			Map<String, Object> payload = new HashMap<>();
			payload.put("iss", properties.getIssuer());
			payload.put("sub", String.valueOf(userId));
			payload.put("account", account);
			payload.put("iat", now);
			payload.put("nbf", now);
			payload.put("exp", exp);
			payload.put("jti", UUID.randomUUID().toString().replace("-", ""));

			String encodedHeader = encode(objectMapper.writeValueAsBytes(header));
			String encodedPayload = encode(objectMapper.writeValueAsBytes(payload));
			String content = encodedHeader + "." + encodedPayload;
			String sign = sign(content);
			return content + "." + sign;
		}
		catch (Exception ex) {
			throw new IllegalStateException("生成JWT失败", ex);
		}
	}

	public Map<String, Object> parseAndValidate(String token) {
		try {
			String[] parts = token.split("\\.");
			if (parts.length != 3) {
				return null;
			}

			String content = parts[0] + "." + parts[1];
			String expected = sign(content);
			if (!MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8),
					parts[2].getBytes(StandardCharsets.UTF_8))) {
				return null;
			}

			Map<String, Object> payload = objectMapper.readValue(decode(parts[1]), new TypeReference<>() {
			});
			Object expObj = payload.get("exp");
			if (expObj instanceof Number number && number.longValue() < Instant.now().getEpochSecond()) {
				return null;
			}
			return payload;
		}
		catch (Exception ex) {
			return null;
		}
	}

	private String sign(String data) throws Exception {
		if (!StringUtils.hasText(properties.getSecret())) {
			throw new IllegalStateException("oa.php-jwt.secret 未配置");
		}
		Mac mac = Mac.getInstance(HMAC_SHA256);
		mac.init(new SecretKeySpec(properties.getSecret().getBytes(StandardCharsets.UTF_8), HMAC_SHA256));
		return encode(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
	}

	private String encode(byte[] bytes) {
		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}

	private byte[] decode(String value) {
		return Base64.getUrlDecoder().decode(value);
	}

}
