package com.bubblecloud.biz.oa.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.bubblecloud.biz.oa.constant.config.OaPhpJwtProperties;
import com.bubblecloud.biz.oa.service.OaDownloadSigningService;
import com.bubblecloud.oa.api.dto.common.OaDownloadPayloadDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;

/**
 * {@link OaDownloadSigningService} 实现：payload JSON 的 Base64URL + HMAC-SHA256。
 *
 * @author qinlei
 * @date 2026/4/4 18:00
 */
@Service
@RequiredArgsConstructor
public class OaDownloadSigningServiceImpl implements OaDownloadSigningService {

	private static final String HMAC_ALG = "HmacSHA256";

	private final ObjectMapper objectMapper;

	private final OaPhpJwtProperties phpJwtProperties;

	@Override
	public String sign(OaDownloadPayloadDTO payload) {
		try {
			byte[] json = objectMapper.writeValueAsBytes(payload);
			String b64 = Base64.getUrlEncoder().withoutPadding().encodeToString(json);
			Mac mac = Mac.getInstance(HMAC_ALG);
			mac.init(new SecretKeySpec(signingKey(), HMAC_ALG));
			String sig = Base64.getUrlEncoder()
				.withoutPadding()
				.encodeToString(mac.doFinal(b64.getBytes(StandardCharsets.UTF_8)));
			return b64 + "." + sig;
		}
		catch (Exception e) {
			throw new IllegalStateException("下载签名失败", e);
		}
	}

	@Override
	public OaDownloadPayloadDTO verify(String signature) {
		if (StrUtil.isBlank(signature)) {
			throw new IllegalArgumentException("缺少签名");
		}
		int dot = signature.indexOf('.');
		if (dot <= 0 || dot == signature.length() - 1) {
			throw new IllegalArgumentException("签名格式错误");
		}
		String b64 = signature.substring(0, dot);
		String sig = signature.substring(dot + 1);
		try {
			Mac mac = Mac.getInstance(HMAC_ALG);
			mac.init(new SecretKeySpec(signingKey(), HMAC_ALG));
			byte[] expect = mac.doFinal(b64.getBytes(StandardCharsets.UTF_8));
			byte[] got = Base64.getUrlDecoder().decode(sig);
			if (!java.security.MessageDigest.isEqual(expect, got)) {
				throw new IllegalArgumentException("签名校验失败");
			}
			byte[] json = Base64.getUrlDecoder().decode(b64);
			return objectMapper.readValue(json, OaDownloadPayloadDTO.class);
		}
		catch (NoSuchAlgorithmException | InvalidKeyException e) {
			throw new IllegalStateException(e);
		}
		catch (IllegalArgumentException e) {
			throw e;
		}
		catch (Exception e) {
			throw new IllegalArgumentException("签名解析失败", e);
		}
	}

	private byte[] signingKey() {
		String s = StrUtil.blankToDefault(phpJwtProperties.getSecret(), "oa-download-dev-key-change-me");
		return DigestUtil.sha256(s);
	}

}
