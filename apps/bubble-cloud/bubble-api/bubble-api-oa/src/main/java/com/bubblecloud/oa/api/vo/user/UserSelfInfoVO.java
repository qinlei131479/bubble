package com.bubblecloud.oa.api.vo.user;

import lombok.Data;

/**
 * GET /ent/user/userInfo 当前用户基本信息（对齐 PHP AdminService::getInfo）。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@Data
public class UserSelfInfoVO {

	private Long id;

	private String uid;

	private String password;

	private String phone;

	private String name;

	private String avatar;

	private String email;

}
