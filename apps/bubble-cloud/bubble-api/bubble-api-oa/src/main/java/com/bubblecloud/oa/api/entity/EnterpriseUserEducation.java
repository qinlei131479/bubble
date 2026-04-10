package com.bubblecloud.oa.api.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bubblecloud.common.mybatis.base.Req;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 企业员工教育经历，对应 eb_enterprise_user_education 表。
 *
 * @author qinlei
 * @date 2026/4/5
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "企业员工教育经历")
@TableName("eb_enterprise_user_education")
public class EnterpriseUserEducation extends Req<EnterpriseUserEducation> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "用户ID")
	private Long userId;

	@Schema(description = "名片/员工 admin.id")
	private Long cardId;

	@Schema(description = "开始时间")
	private LocalDate startTime;

	@Schema(description = "结束时间")
	private LocalDate endTime;

	@Schema(description = "学校名称")
	private String schoolName;

	@Schema(description = "专业")
	private String major;

	@Schema(description = "学历")
	private String education;

	@Schema(description = "学位")
	private String academic;

	@Schema(description = "备注")
	private String remark;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

}
