package com.bubblecloud.oa.api.entity;

import java.time.LocalDate;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bubblecloud.common.mybatis.base.Req;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 个人简历-教育经历，对应 eb_user_education_history。
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "个人简历教育经历")
@TableName("eb_user_education_history")
public class UserEducationHistory extends Req<UserEducationHistory> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	private Long id;

	private String uid;

	private Long resumeId;

	private LocalDate startTime;

	private LocalDate endTime;

	private String schoolName;

	private String major;

	private String education;

	private String academic;

	private String remark;

}
