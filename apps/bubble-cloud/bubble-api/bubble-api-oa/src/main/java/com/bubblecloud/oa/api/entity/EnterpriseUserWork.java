package com.bubblecloud.oa.api.entity;

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
 * 企业员工工作经历，对应 eb_enterprise_user_work 表。
 *
 * @author qinlei
 * @date 2026/4/5
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "企业员工工作经历")
@TableName("eb_enterprise_user_work")
public class EnterpriseUserWork extends Req<EnterpriseUserWork> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "用户ID")
	private Long userId;

	@Schema(description = "名片/员工 admin.id")
	private Long cardId;

	@Schema(description = "入职时间")
	private LocalDate startTime;

	@Schema(description = "离职时间")
	private LocalDate endTime;

	@Schema(description = "公司名称")
	private String company;

	@Schema(description = "岗位名称")
	private String position;

	@Schema(description = "工作描述")
	@TableField("`describe`")
	private String describe;

	@Schema(description = "离职原因")
	private String quitReason;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

}
