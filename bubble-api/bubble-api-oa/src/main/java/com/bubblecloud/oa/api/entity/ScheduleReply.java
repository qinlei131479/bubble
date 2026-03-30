package com.bubblecloud.oa.api.entity;

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
 * 日程评价，对应 eb_schedule_reply。
 *
 * @author qinlei
 * @date 2026/3/29 18:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "日程评价")
@TableName("eb_schedule_reply")
public class ScheduleReply extends Req<ScheduleReply> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	private Long id;

	private Long uid;

	private Long pid;

	@TableField("reply_id")
	private Long replyId;

	@TableField("to_uid")
	private Long toUid;

	@TableField("start_time")
	private LocalDateTime startTime;

	@TableField("end_time")
	private LocalDateTime endTime;

	private String content;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

}
