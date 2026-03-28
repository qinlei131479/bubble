package com.bubblecloud.biz.oa.service;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.entity.Admin;

/**
 * eb_admin 员工账号服务。
 */
public interface AdminService extends UpService<Admin> {

	/**
	 * 按账号或手机号查询员工。
	 * @param account 账号或手机号
	 * @return 员工
	 */
	Admin getByAccount(String account);

}
