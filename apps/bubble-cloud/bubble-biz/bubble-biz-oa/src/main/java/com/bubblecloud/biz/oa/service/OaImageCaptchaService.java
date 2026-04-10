package com.bubblecloud.biz.oa.service;

import com.bubblecloud.oa.api.vo.CaptchaVO;

/**
 * 图形验证码（Redis 存明文，对齐 PHP mews/captcha 使用场景）。
 *
 * @author qinlei
 * @date 2026/4/4 16:00
 */
public interface OaImageCaptchaService {

	/**
	 * 生成验证码，返回 key 与 Base64 图片（data URL）。
	 */
	CaptchaVO create();

	/**
	 * 校验并删除缓存（一次性）。
	 */
	boolean verifyAndConsume(String key, String userCode);

}
