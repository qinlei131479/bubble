package com.pig4cloud.pig.admin.service;

import com.pig4cloud.pig.common.core.util.R;

/**
 * 系统手机服务接口：提供手机验证码发送功能
 *
 * @author lengleng
 * @date 2025/05/30
 */
public interface SysMobileService {

	/**
	 * 发送手机验证码
	 * @param mobile 手机号码
	 * @return 发送结果，成功返回true，失败返回false
	 */
	R<Boolean> sendSmsCode(String mobile);

}
