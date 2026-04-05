package com.bubblecloud.oa.api.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bubblecloud.common.mybatis.base.Req;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 企业员工工作分析（岗位职责富文本），对应 eb_enterprise_user_job_analysis 表。
 *
 * @author qinlei
 * @date 2026/4/5 15:30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "企业员工工作分析")
@TableName("eb_enterprise_user_job_analysis")
public class EnterpriseUserJobAnalysis extends Req<EnterpriseUserJobAnalysis> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "企业ID")
	private Long entid;

	/**
	 * 对应 eb_admin.id（与 PHP 字段名 uid 一致，非 admin.uid 字符串）。
	 */
	@TableField("uid")
	@Schema(description = "员工 admin 主键 ID")
	private Long userId;

	@Schema(description = "分析内容（HTML）")
	private String data;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

}
