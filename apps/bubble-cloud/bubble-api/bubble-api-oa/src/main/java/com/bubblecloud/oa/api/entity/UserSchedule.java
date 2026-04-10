package com.bubblecloud.oa.api.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bubblecloud.common.mybatis.base.Req;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户日程与待办，对应 eb_user_schedule 表。
 *
 * @author qinlei
 * @date 2026/3/29 20:30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "用户日程待办")
@TableName("eb_user_schedule")
public class UserSchedule extends Req<UserSchedule> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "用户标识")
	private String uid;

	@Schema(description = "企业ID")
	private Integer entid;

	@Schema(description = "类型")
	private String types;

	@Schema(description = "内容")
	private String content;

	@Schema(description = "备注")
	private String mark;

	@Schema(description = "是否提醒")
	private Integer remind;

	@Schema(description = "重复规则")
	private Integer repeat;

	@Schema(description = "周期")
	private Integer period;

	@Schema(description = "频率")
	private Integer rate;

	@Schema(description = "提醒日期")
	@TableField("remind_day")
	private LocalDate remindDay;

	@Schema(description = "提醒时刻")
	@TableField("remind_time")
	private LocalTime remindTime;

	@Schema(description = "重复日配置")
	private String days;

	@Schema(description = "结束时间")
	@TableField("end_time")
	private LocalDateTime endTime;

	@Schema(description = "关联业务ID")
	@TableField("link_id")
	private Long linkId;

	@Schema(description = "唯一键")
	private String uniqued;

	@Schema(description = "上次触发时间")
	@TableField("last_time")
	private LocalDateTime lastTime;

	@Schema(description = "是否已提醒")
	@TableField("is_remind")
	private Integer isRemind;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

}
