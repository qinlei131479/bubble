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
 * 日程提醒，对应 eb_schedule_remind 表。
 *
 * @author qinlei
 * @date 2026/3/29 18:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "日程提醒")
@TableName("eb_schedule_remind")
public class ScheduleRemind extends Req<ScheduleRemind> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "关联日程或提醒业务ID")
	private Long sid;

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

	@Schema(description = "删除时间")
	@TableField("deleted_at")
	private LocalDateTime deletedAt;

}
