package com.bubblecloud.biz.oa.service;

import com.bubblecloud.oa.api.dto.LoginDTO;
import com.bubblecloud.oa.api.vo.LoginInfoVO;
import com.bubblecloud.oa.api.vo.LoginVO;

/**
 * 兼容 PHP 的登录鉴权服务。
 */
public interface AuthService {

	/**
	 * 账号密码登录。
	 * @param dto 登录参数
	 * @return 登录结果
	 */
	LoginVO login(LoginDTO dto);

	/**
	 * 当前登录用户会话信息（与 PHP AdminService::loginInfo 一致）。
	 * @param userId 用户主键 eb_admin.id
	 * @return 用户信息，不存在返回 null
	 */
	LoginInfoVO loginInfo(Long userId);

	/**
	 * 修改密码（与 PHP AdminService::password 一致）。
	 * @param userId 当前登录用户 id
	 * @param phone 手机号（需与账号一致）
	 * @param newPassword 新密码
	 */
	void updatePassword(Long userId, String phone, String newPassword);

}
