package com.bubblecloud.biz.oa.service.impl;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import com.bubblecloud.biz.oa.service.SmsVerifyService;
import com.bubblecloud.oa.api.dto.SmsVerifySendDTO;
import com.bubblecloud.oa.api.vo.SmsVerifyKeyVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 短信验证码实现：验证码写入 Redis，不调用真实短信网关（开发/对齐环境可查看日志或 Redis）。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SmsVerifyServiceImpl implements SmsVerifyService {

	private static final String KEY_PREFIX_SEND = "oa:sms:sendkey:";

	private static final String KEY_PREFIX_CODE = "oa:sms:code:";

	private static final String KEY_PREFIX_SEND_COUNT = "oa:sms:sendcount:";

	private static final int CODE_TTL_SECONDS = 300;

	private static final int SEND_KEY_TTL_SECONDS = 300;

	private final StringRedisTemplate stringRedisTemplate;

	private final SecureRandom random = new SecureRandom();

	@Override
	public SmsVerifyKeyVO createSendKey() {
		String key = java.util.UUID.randomUUID().toString().replace("-", "");
		String redisKey = KEY_PREFIX_SEND + key;
		stringRedisTemplate.opsForValue().set(redisKey, "1", SEND_KEY_TTL_SECONDS, TimeUnit.SECONDS);
		String expireTime = LocalDateTime.now()
			.plusMinutes(5)
			.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		return new SmsVerifyKeyVO(key, expireTime);
	}

	@Override
	public void sendVerifyCode(SmsVerifySendDTO dto) {
		if (!StringUtils.hasText(dto.getKey())) {
			throw new IllegalArgumentException("请先获取短信发送KEY");
		}
		String sendKeyRedis = KEY_PREFIX_SEND + dto.getKey();
		if (!Boolean.TRUE.equals(stringRedisTemplate.hasKey(sendKeyRedis))) {
			throw new IllegalArgumentException("短信发送KEY已失效请重新获取");
		}
		stringRedisTemplate.delete(sendKeyRedis);

		String phone = dto.getPhone();
		String codeKey = KEY_PREFIX_CODE + phone;
		if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(codeKey))) {
			throw new IllegalArgumentException("当前手机号验证码已发送,验证码在有效期内可重复使用!");
		}

		String code = String.format("%06d", random.nextInt(1_000_000));
		stringRedisTemplate.opsForValue().set(codeKey, code, CODE_TTL_SECONDS, TimeUnit.SECONDS);

		String dayKey = KEY_PREFIX_SEND_COUNT + phone + ":" + LocalDateTime.now().toLocalDate();
		Long cnt = stringRedisTemplate.opsForValue().increment(dayKey);
		if (cnt != null && cnt == 1L) {
			stringRedisTemplate.expire(dayKey, 1, TimeUnit.DAYS);
		}
		if (cnt != null && cnt > 20) {
			stringRedisTemplate.delete(codeKey);
			throw new IllegalArgumentException("当前手机号今日发送验证码已达上限!");
		}

		log.info("[OA SMS] phone={} code={} (未接短信网关，生产环境请对接 sms4j/云厂商)", phone, code);
	}

	@Override
	public boolean verifyCode(String phone, String code) {
		if (!StringUtils.hasText(phone) || !StringUtils.hasText(code)) {
			return false;
		}
		String codeKey = KEY_PREFIX_CODE + phone;
		String cached = stringRedisTemplate.opsForValue().get(codeKey);
		if (cached == null) {
			return false;
		}
		if (!cached.equals(code.trim())) {
			return false;
		}
		stringRedisTemplate.delete(codeKey);
		return true;
	}

}
