package com.bubblecloud.oa.api.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
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
 * 调薪记录，对应 eb_enterprise_user_salary 表。
 *
 * @author qinlei
 * @date 2026/3/29 下午6:30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "调薪记录")
@TableName("eb_enterprise_user_salary")
public class EnterpriseUserSalary extends Req<EnterpriseUserSalary> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "企业ID")
	private Long entid;

	@Schema(description = "员工档案 card_id")
	@TableField("card_id")
	private Long cardId;

	@Schema(description = "变更金额")
	private BigDecimal total;

	@Schema(description = "生效日期")
	@TableField("take_date")
	private LocalDate takeDate;

	@Schema(description = "变更内容")
	private String content;

	@Schema(description = "变更原因")
	private String mark;

	@Schema(description = "关联申请单ID")
	@TableField("link_id")
	private Integer linkId;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

}
