package com.bubblecloud.oa.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bubblecloud.common.mybatis.base.Req;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 企业角色与员工关联，对应 eb_enterprise_role_user 表。
 *
 * @author qinlei
 * @date 2026/3/30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "企业角色用户关联")
@TableName("eb_enterprise_role_user")
public class EnterpriseRoleUser extends Req<EnterpriseRoleUser> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "企业ID")
	private Integer entid;

	@Schema(description = "角色ID")
	private Integer roleId;

	@Schema(description = "员工 admin.id")
	private Integer userId;

	@Schema(description = "状态 1 启用 0 禁用")
	private Integer status;

}
