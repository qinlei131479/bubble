package com.bubblecloud.biz.backend.service;

import com.bubblecloud.common.core.util.R;

/**
 * @author lengleng
 * @date 2018/11/14
 */
public interface SysMobileService {

	/**
	 * 发送手机验证码
	 * @param mobile mobile
	 * @return code
	 */
	R<Boolean> sendSmsCode(String mobile);

}
