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
 * 企业员工教育经历，对应 eb_enterprise_user_education。
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "企业员工教育经历")
@TableName("eb_enterprise_user_education")
public class EnterpriseUserEducation extends Req<EnterpriseUserEducation> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	private Long id;

	private Long userId;

	private Long cardId;

	private LocalDate startTime;

	private LocalDate endTime;

	private String schoolName;

	private String major;

	private String education;

	private String academic;

	private String remark;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

}
