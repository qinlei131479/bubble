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
 * 企业角色，对应 eb_enterprise_role 表。
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "企业角色")
@TableName("eb_enterprise_role")
public class EnterpriseRole extends Req<EnterpriseRole> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "角色名称")
	private String roleName;

	@Schema(description = "菜单权限ID列表（JSON数组）")
	private String rules;

	@Schema(description = "接口权限ID列表（JSON数组）")
	private String apis;

	@Schema(description = "企业ID")
	private Integer entid;

	@Schema(description = "状态：1启用 0禁用")
	private Integer status;

	@Schema(description = "备注")
	private String mark;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

	@Schema(description = "删除时间")
	private LocalDateTime deletedAt;

}
