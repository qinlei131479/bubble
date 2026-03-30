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
 * 日程周期任务（完成状态），对应 eb_schedule_task。
 *
 * @author qinlei
 * @date 2026/3/29 18:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "日程任务实例")
@TableName("eb_schedule_task")
public class ScheduleTask extends Req<ScheduleTask> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	private Long id;

	private Long uid;

	private Long pid;

	@TableField("start_time")
	private LocalDateTime startTime;

	@TableField("end_time")
	private LocalDateTime endTime;

	private Integer status;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

}
