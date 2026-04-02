package com.bubblecloud.biz.oa.service;

import com.bubblecloud.oa.api.dto.ConfigQueryDTO;
import com.bubblecloud.oa.api.dto.SmsVerifySendDTO;
import com.bubblecloud.oa.api.vo.CaptchaVO;
import com.bubblecloud.oa.api.vo.ConfigVO;
import com.bubblecloud.oa.api.vo.SiteVO;
import com.bubblecloud.oa.api.vo.SmsVerifyKeyVO;
import com.bubblecloud.oa.api.vo.common.CommonAuthVO;
import com.bubblecloud.oa.api.vo.common.CommonMessageVO;
import com.bubblecloud.oa.api.vo.common.CommonVersionVO;
import org.springframework.security.core.Authentication;

/**
 * OA 公共接口聚合业务（对齐 {@code CommonController} 路由）。
 *
 * @author qinlei
 * @date 2026/3/31
 */
public interface CommonService {

	CaptchaVO captcha();

	ConfigVO config(ConfigQueryDTO dto);

	SiteVO site();

	CommonAuthVO auth();

	SmsVerifyKeyVO verifyKey();

	/**
	 * 发送短信验证码；业务校验失败抛出 {@link IllegalArgumentException}。
	 */
	void sendVerifySms(SmsVerifySendDTO dto);

	CommonMessageVO messageList(Integer page, Integer limit, String cateId, String title);

	/**
	 * 更新消息已读；未登录或用户不存在抛出 {@link IllegalArgumentException}。
	 */
	void updateMessageRead(Long messageId, Integer isRead);

	CommonVersionVO version();

}
