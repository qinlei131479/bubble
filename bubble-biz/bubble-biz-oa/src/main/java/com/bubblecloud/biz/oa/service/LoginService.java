package com.bubblecloud.biz.oa.service;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.entity.Admin;
import com.bubblecloud.oa.api.dto.LoginDTO;
import com.bubblecloud.oa.api.dto.PhoneLoginDTO;
import com.bubblecloud.oa.api.dto.RegisterDTO;
import com.bubblecloud.oa.api.vo.LoginInfoVO;
import com.bubblecloud.oa.api.vo.LoginVO;
import com.bubblecloud.oa.api.vo.ScanKeyVO;
import com.bubblecloud.oa.api.vo.ScanStatusResultVO;

/**
 * 兼容 PHP 的登录鉴权服务。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
public interface LoginService extends UpService<Admin> {

	/**
	 * 账号密码登录。
	 * @param dto 登录参数
	 * @return 登录结果
	 */
	LoginVO login(LoginDTO dto);

	/**
	 * 用户注册（短信验证码 + 密码），成功后返回与登录一致的 token。
	 */
	LoginVO register(RegisterDTO dto);

	/**
	 * 短信验证码登录（验证码校验通过后；若用户不存在则自动注册，对齐 PHP phone_login）。
	 */
	LoginVO phoneLogin(PhoneLoginDTO dto);

	/**
	 * 已通过扫码或短信二次校验后，按手机号签发 token（不校验密码）。
	 */
	LoginVO loginByPhone(String phone);

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


	ScanKeyVO createScanKey();

	ScanStatusResultVO pollStatus(String key);
}
