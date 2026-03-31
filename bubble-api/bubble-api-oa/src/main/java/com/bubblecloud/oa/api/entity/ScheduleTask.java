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
 * 日程周期任务实例，对应 eb_schedule_task 表。
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
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "用户 admin.id")
	private Long uid;

	@Schema(description = "父日程ID")
	private Long pid;

	@Schema(description = "开始时间")
	@TableField("start_time")
	private LocalDateTime startTime;

	@Schema(description = "结束时间")
	@TableField("end_time")
	private LocalDateTime endTime;

	@Schema(description = "完成状态")
	private Integer status;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

}
