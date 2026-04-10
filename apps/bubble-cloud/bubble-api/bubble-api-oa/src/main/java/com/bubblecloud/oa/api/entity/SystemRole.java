package com.bubblecloud.oa.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bubblecloud.common.mybatis.base.Req;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统身份/超级角色权限，对应 eb_system_role 表（企业默认权限模板 type=0/空 entid=0；企业超级角色 type=enterprise）。
 *
 * @author qinlei
 * @date 2026/4/6 15:30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "系统角色权限")
@TableName("eb_system_role")
public class SystemRole extends Req<SystemRole> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "身份名称")
	private String roleName;

	@Schema(description = "菜单权限 ID 列表 JSON")
	private String rules;

	@Schema(description = "接口权限 ID 列表 JSON")
	private String apis;

	@Schema(description = "类型：enterprise 企业超级；0 或空为平台模板等")
	private String type;

	@Schema(description = "企业ID，0 表示平台")
	private Long entid;

	@Schema(description = "等级")
	private Integer level;

	@Schema(description = "状态")
	private Integer status;

	@Schema(description = "企业唯一值")
	private String uniqued;

}
