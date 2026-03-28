package com.bubblecloud.oa.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 当前登录用户信息。
 *
 * @author qinlei
 */
@Data
@Schema(description = "当前登录用户")
public class CurrentUserVO {

	@Schema(description = "用户ID")
	private Long id;

	@Schema(description = "用户UID")
	private String uid;

	@Schema(description = "账号")
	private String account;

	@Schema(description = "姓名")
	private String name;

	@Schema(description = "手机号")
	private String phone;

	@Schema(description = "头像")
	private String avatar;

	@Schema(description = "状态")
	private Integer status;

}
