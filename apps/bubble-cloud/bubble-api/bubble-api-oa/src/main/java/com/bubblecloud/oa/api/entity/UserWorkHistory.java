package com.bubblecloud.oa.api.entity;

import java.time.LocalDate;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bubblecloud.common.mybatis.base.Req;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 个人简历工作经历，对应 eb_user_work_history 表。
 *
 * @author qinlei
 * @date 2026/4/5
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "个人简历工作经历")
@TableName("eb_user_work_history")
public class UserWorkHistory extends Req<UserWorkHistory> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "用户UID")
	private String uid;

	@Schema(description = "简历ID")
	private Long resumeId;

	@Schema(description = "开始时间")
	private LocalDate startTime;

	@Schema(description = "结束时间")
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

}
