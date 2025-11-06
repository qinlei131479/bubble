package com.bubblecloud.backend.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.backend.api.entity.SysUser;
import com.bubblecloud.backend.mapper.SysUserMapper;
import com.bubblecloud.backend.service.SysMobileService;
import com.bubblecloud.common.core.constant.CacheConstants;
import com.bubblecloud.common.core.constant.SecurityConstants;
import com.bubblecloud.common.core.exception.ErrorCodes;
import com.bubblecloud.common.core.util.MsgUtils;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.core.util.RedisUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.sms4j.api.SmsBlend;
import org.dromara.sms4j.api.entity.SmsResponse;
import org.dromara.sms4j.core.factory.SmsFactory;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 手机登录相关业务实现类
 *
 * @author lengleng
 * @date 2025/05/30
 */
@Slf4j
@Service
@AllArgsConstructor
public class SysMobileServiceImpl implements SysMobileService {

	private final SysUserMapper userMapper;

	/**
	 * 发送手机验证码
	 * @param mobile 手机号码
	 * @return 返回操作结果，包含验证码发送状态及验证码信息
	 */
	@Override
	public R<Boolean> sendSmsCode(String mobile) {
		List<SysUser> userList = userMapper
			.selectList(Wrappers.<SysUser>query().lambda().eq(SysUser::getPhone, mobile));

		if (CollUtil.isEmpty(userList)) {
			log.info("手机号未注册:{}", mobile);
			return R.ok(Boolean.FALSE, MsgUtils.getMessage(ErrorCodes.SYS_APP_PHONE_UNREGISTERED, mobile));
		}

		String cacheKey = CacheConstants.DEFAULT_CODE_KEY + mobile;
		String codeObj = RedisUtils.get(cacheKey);

		if (codeObj != null) {
			log.info("手机号验证码未过期:{}，{}", mobile, codeObj);
			return R.ok(Boolean.FALSE, MsgUtils.getMessage(ErrorCodes.SYS_APP_SMS_OFTEN));
		}

		String code = RandomUtil.randomNumbers(Integer.parseInt(SecurityConstants.CODE_SIZE));
		log.info("手机号生成验证码成功:{},{}", mobile, code);
		RedisUtils.set(cacheKey, code, SecurityConstants.CODE_TIME, TimeUnit.SECONDS);

		// 集成短信服务发送验证码
		SmsBlend smsBlend = SmsFactory.getSmsBlend();
		if (Objects.isNull(smsBlend)) {
			return R.ok(Boolean.FALSE, MsgUtils.getMessage(ErrorCodes.SYS_SMS_BLEND_UNREGISTERED));
		}

		SmsResponse smsResponse = smsBlend.sendMessage(mobile, new LinkedHashMap<>(Map.of("code", code)));
		log.debug("调用短信服务发送验证码结果:{}", smsResponse);
		return R.ok(Boolean.TRUE);
	}

}
