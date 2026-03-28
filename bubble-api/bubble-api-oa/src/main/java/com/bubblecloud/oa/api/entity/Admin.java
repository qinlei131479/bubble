package com.bubblecloud.oa.api.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bubblecloud.common.mybatis.base.Req;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 员工账号，对应 eb_admin 表。
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "员工账号")
@TableName("eb_admin")
public class Admin extends Req<Admin> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "用户UID")
	private String uid;

	@Schema(description = "账号")
	private String account;

	@Schema(description = "密码")
	private String password;

	@Schema(description = "头像")
	private String avatar;

	@Schema(description = "姓名")
	private String name;

	@Schema(description = "手机号")
	private String phone;

	@Schema(description = "岗位ID")
	private Integer job;

	@Schema(description = "是否超级管理员")
	private Integer isAdmin;

	@Schema(description = "角色ID列表（JSON数组）")
	private String roles;

	@Schema(description = "登录状态")
	private Integer uniOnline;

	@Schema(description = "客户端ID")
	private String clientId;

	@Schema(description = "扫码Key")
	private String scanKey;

	@Schema(description = "最后登录IP")
	private String lastIp;

	@Schema(description = "登录次数")
	private Integer loginCount;

	@Schema(description = "状态")
	private Integer status;

	@Schema(description = "是否初始密码")
	private Integer isInit;

	@Schema(description = "语言")
	private String language;

	@Schema(description = "备注")
	private String mark;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

	@Schema(description = "删除时间")
	private LocalDateTime deletedAt;

}
