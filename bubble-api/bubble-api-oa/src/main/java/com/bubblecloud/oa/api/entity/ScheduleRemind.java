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
 * 日程提醒，对应 eb_schedule_remind。
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
	private Long id;

	private Long sid;

	private String uid;

	private Integer entid;

	private String types;

	private String content;

	private String mark;

	private Integer period;

	private Integer rate;

	@TableField("remind_day")
	private LocalDate remindDay;

	@TableField("remind_time")
	private LocalTime remindTime;

	private String days;

	@TableField("end_time")
	private LocalDateTime endTime;

	private String uniqued;

	@TableField("last_time")
	private LocalDateTime lastTime;

	@TableField("is_remind")
	private Integer isRemind;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	@TableField("deleted_at")
	private LocalDateTime deletedAt;

}
