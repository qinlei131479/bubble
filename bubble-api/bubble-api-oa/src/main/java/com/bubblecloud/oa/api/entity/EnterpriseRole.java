package com.bubblecloud.oa.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bubblecloud.common.mybatis.base.Req;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 企业角色，对应 eb_enterprise_role 表。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
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

	@Schema(description = "角色类型")
	private String types;

	@Schema(description = "用户数量")
	private Integer userCount;

	@Schema(description = "菜单权限ID列表（JSON数组）")
	private String rules;

	@Schema(description = "菜单唯一标识")
	private String ruleUnique;

	@Schema(description = "接口权限ID列表（JSON数组）")
	private String apis;

	@Schema(description = "接口唯一标识")
	private String apiUnique;

	@Schema(description = "企业ID")
	private Integer entid;

	@Schema(description = "数据范围")
	private Integer dataLevel;

	@Schema(description = "是否包含直属下级")
	private Integer directly;

	@Schema(description = "指定部门ID（JSON或字符串）")
	private String frameId;

	@Schema(description = "状态：1启用 0禁用")
	private Integer status;

	@Schema(description = "备注")
	private String mark;

}
