package com.bubblecloud.biz.oa.service;

import com.bubblecloud.oa.api.dto.SmsVerifySendDTO;
import com.bubblecloud.oa.api.vo.SmsVerifyKeyVO;

/**
 * 短信验证码发送与校验（Redis），对齐 PHP CommonService::smsVerifyKey / smsVerifyCode。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
public interface SmsVerifyService {

	SmsVerifyKeyVO createSendKey();

	void sendVerifyCode(SmsVerifySendDTO dto);

	boolean verifyCode(String phone, String code);

}
