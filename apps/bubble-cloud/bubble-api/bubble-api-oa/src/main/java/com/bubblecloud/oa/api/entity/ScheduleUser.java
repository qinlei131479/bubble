package com.bubblecloud.oa.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bubblecloud.common.mybatis.base.Req;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 日程参与人，对应 eb_schedule_user 表。
 *
 * @author qinlei
 * @date 2026/3/29 18:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "日程参与人")
@TableName("eb_schedule_user")
public class ScheduleUser extends Req<ScheduleUser> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "参与人 admin.id")
	private Long uid;

	@Schema(description = "日程ID")
	@TableField("schedule_id")
	private Long scheduleId;

	@Schema(description = "是否负责人")
	@TableField("is_master")
	private Integer isMaster;

}
