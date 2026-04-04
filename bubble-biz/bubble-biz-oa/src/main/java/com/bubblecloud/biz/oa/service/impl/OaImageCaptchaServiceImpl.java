package com.bubblecloud.biz.oa.service.impl;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.bubblecloud.biz.oa.service.OaImageCaptchaService;
import com.bubblecloud.oa.api.vo.CaptchaVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * {@link OaImageCaptchaService} 实现。
 *
 * @author qinlei
 * @date 2026/4/4 16:00
 */
@Service
@RequiredArgsConstructor
public class OaImageCaptchaServiceImpl implements OaImageCaptchaService {

	private static final String REDIS_PREFIX = "oa:captcha:img:";

	private static final long TTL_SECONDS = 120L;

	private final StringRedisTemplate stringRedisTemplate;

	@Override
	public CaptchaVO create() {
		LineCaptcha cap = CaptchaUtil.createLineCaptcha(130, 48, 4, 20);
		String code = cap.getCode();
		String key = IdUtil.fastSimpleUUID();
		stringRedisTemplate.opsForValue()
			.set(REDIS_PREFIX + key, code.toLowerCase(Locale.ROOT), TTL_SECONDS, TimeUnit.SECONDS);
		String img = "data:image/png;base64," + cap.getImageBase64();
		return new CaptchaVO(key, img);
	}

	@Override
	public boolean verifyAndConsume(String key, String userCode) {
		if (StrUtil.hasBlank(key, userCode)) {
			return false;
		}
		String redisKey = REDIS_PREFIX + key;
		String cached = stringRedisTemplate.opsForValue().get(redisKey);
		if (StrUtil.isBlank(cached)) {
			return false;
		}
		stringRedisTemplate.delete(redisKey);
		return cached.equals(userCode.trim().toLowerCase(Locale.ROOT));
	}

}
