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
 * 企业员工工作经历，对应 eb_enterprise_user_work。
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "企业员工工作经历")
@TableName("eb_enterprise_user_work")
public class EnterpriseUserWork extends Req<EnterpriseUserWork> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	private Long id;

	private Long userId;

	private Long cardId;

	private LocalDate startTime;

	private LocalDate endTime;

	private String company;

	private String position;

	@TableField("`describe`")
	private String describe;

	private String quitReason;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

}
