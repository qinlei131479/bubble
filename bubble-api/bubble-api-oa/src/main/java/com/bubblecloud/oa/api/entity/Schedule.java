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
 * 日程主表，对应 eb_schedule 表。
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
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "创建人 admin.id")
	private Long uid;

	@Schema(description = "日历/分类ID")
	private Long cid;

	@Schema(description = "颜色")
	private String color;

	@Schema(description = "标题")
	private String title;

	@Schema(description = "内容")
	private String content;

	@Schema(description = "是否全天")
	@TableField("all_day")
	private Integer allDay;

	@Schema(description = "开始时间")
	@TableField("start_time")
	private LocalDateTime startTime;

	@Schema(description = "结束时间")
	@TableField("end_time")
	private LocalDateTime endTime;

	@Schema(description = "周期")
	private Integer period;

	@Schema(description = "频率")
	private Integer rate;

	@Schema(description = "重复日配置")
	private String days;

	@Schema(description = "提醒方式")
	private Integer remind;

	@Schema(description = "失败/超时时间")
	@TableField("fail_time")
	private LocalDateTime failTime;

	@Schema(description = "父日程ID")
	private Long pid;

	@Schema(description = "关联业务ID")
	@TableField("link_id")
	private Long linkId;

	@Schema(description = "状态")
	private Integer status;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

}
