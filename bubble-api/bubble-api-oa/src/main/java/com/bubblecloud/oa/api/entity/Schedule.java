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
 * 日程主表，对应 eb_schedule。
 *
 * @author qinlei
 * @date 2026/3/29 18:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "日程")
@TableName("eb_schedule")
public class Schedule extends Req<Schedule> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	private Long id;

	@Schema(description = "创建人 admin.id")
	private Long uid;

	private Long cid;

	private String color;

	private String title;

	private String content;

	@TableField("all_day")
	private Integer allDay;

	@TableField("start_time")
	private LocalDateTime startTime;

	@TableField("end_time")
	private LocalDateTime endTime;

	private Integer period;

	private Integer rate;

	private String days;

	private Integer remind;

	@TableField("fail_time")
	private LocalDateTime failTime;

	private Long pid;

	@TableField("link_id")
	private Long linkId;

	private Integer status;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

}
