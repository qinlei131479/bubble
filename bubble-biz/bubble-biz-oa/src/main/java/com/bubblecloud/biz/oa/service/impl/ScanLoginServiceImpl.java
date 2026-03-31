package com.bubblecloud.biz.oa.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.AdminMapper;
import com.bubblecloud.biz.oa.service.AuthService;
import com.bubblecloud.biz.oa.service.ScanLoginService;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.Admin;
import com.bubblecloud.oa.api.vo.LoginVO;
import com.bubblecloud.oa.api.vo.ScanKeyVO;
import com.bubblecloud.oa.api.vo.ScanStatusResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 扫码登录实现。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@Service
@RequiredArgsConstructor
public class ScanLoginServiceImpl extends UpServiceImpl<AdminMapper, Admin> implements ScanLoginService {

	private static final String KEY_PREFIX = "oa:scan:key:";

	private static final int TTL_SECONDS = 180;

	private final StringRedisTemplate stringRedisTemplate;

	private final AuthService authService;

	@Override
	public ScanKeyVO createScanKey() {
		String key = java.util.UUID.randomUUID().toString().replace("-", "");
		stringRedisTemplate.opsForValue().set(KEY_PREFIX + key, "0", TTL_SECONDS, TimeUnit.SECONDS);
		String expireTime = LocalDateTime.now()
			.plusSeconds(TTL_SECONDS)
			.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		return new ScanKeyVO(key, expireTime);
	}

	@Override
	public ScanStatusResultVO pollStatus(String key) {
		ScanStatusResultVO vo = new ScanStatusResultVO();
		if (StrUtil.isBlank(key)) {
			vo.setStatus(-1);
			vo.setMsg("参数已失效,请重新获取");
			return vo;
		}
		Admin byScan = baseMapper.selectOne(Wrappers.lambdaQuery(Admin.class)
			.eq(Admin::getScanKey, key)
			.isNull(Admin::getDeletedAt)
			.last("LIMIT 1"));
		if (ObjectUtil.isNotNull(byScan) && StrUtil.isNotBlank(byScan.getPhone())) {
			baseMapper.update(null,
					Wrappers.lambdaUpdate(Admin.class).eq(Admin::getId, byScan.getId()).set(Admin::getScanKey, ""));
			stringRedisTemplate.delete(KEY_PREFIX + key);
			LoginVO login = authService.loginByPhone(byScan.getPhone());
			vo.setLogin(login);
			return vo;
		}

		String redisKey = KEY_PREFIX + key;
		String v = stringRedisTemplate.opsForValue().get(redisKey);
		if (ObjectUtil.isNull(v)) {
			vo.setStatus(-1);
			vo.setMsg("参数已失效,请重新获取");
			return vo;
		}
		if (!"0".equals(v)) {
			vo.setStatus(1);
			vo.setMsg("已扫码");
			return vo;
		}
		vo.setStatus(0);
		vo.setMsg("未扫码");
		return vo;
	}

	@Override
	public void bindScanUserId(String key, long userId) {
		String redisKey = KEY_PREFIX + key;
		if (!Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisKey))) {
			return;
		}
		stringRedisTemplate.opsForValue().set(redisKey, String.valueOf(userId), TTL_SECONDS, TimeUnit.SECONDS);
	}

}
