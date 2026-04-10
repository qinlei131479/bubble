package com.bubblecloud.biz.oa.service;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.entity.Admin;

/**
 * eb_admin 员工账号服务。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
public interface AdminService extends UpService<Admin> {

	/**
	 * 按账号或手机号查询员工。
	 * @param account 账号或手机号
	 * @return 员工
	 */
	Admin getByAccount(String account);

	/**
	 * 手机号是否已存在。
	 */
	Long countByPhone(String phone);

	/**
	 * 注册新用户（对齐 PHP AdminService::register）。
	 */
	Admin createRegisteredUser(String phone, String encodedPassword);

	/**
	 * 短信登录时用户不存在则创建（对齐 PHP phone_login 内 register）。
	 */
	Admin ensureUserForPhoneLogin(String phone);

	/**
	 * 按 uid 重置密码（对齐 PHP AdminService::updatePasswordFromUid）。
	 */
	void updatePasswordByUid(String uid, String rawPassword);

}
