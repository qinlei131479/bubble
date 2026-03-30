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
 * 用户待办提醒，对应 eb_user_schedule。
 *
 * @author qinlei
 * @date 2026/3/29 20:30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "用户待办")
@TableName("eb_user_schedule")
public class UserSchedule extends Req<UserSchedule> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	private Long id;

	private String uid;

	private Integer entid;

	private String types;

	private String content;

	private String mark;

	private Integer remind;

	private Integer repeat;

	private Integer period;

	private Integer rate;

	@TableField("remind_day")
	private LocalDate remindDay;

	@TableField("remind_time")
	private LocalTime remindTime;

	private String days;

	@TableField("end_time")
	private LocalDateTime endTime;

	@TableField("link_id")
	private Long linkId;

	private String uniqued;

	@TableField("last_time")
	private LocalDateTime lastTime;

	@TableField("is_remind")
	private Integer isRemind;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

}
