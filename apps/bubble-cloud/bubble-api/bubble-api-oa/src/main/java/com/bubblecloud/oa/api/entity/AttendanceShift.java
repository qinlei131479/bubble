package com.bubblecloud.oa.api.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bubblecloud.common.mybatis.base.Req;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 考勤班次主表，对应 eb_attendance_shift 表。
 *
 * @author qinlei
 * @date 2026/4/7 12:15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "考勤班次")
@TableName("eb_attendance_shift")
public class AttendanceShift extends Req<AttendanceShift> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Integer id;

	@Schema(description = "班次名称")
	private String name;

	@Schema(description = "上下班次数：0 休息；1 一次；2 两次")
	private Integer number;

	@Schema(description = "中途休息 1 开启 0 关闭")
	@TableField("rest_time")
	private Integer restTime;

	@Schema(description = "休息开始时间")
	@TableField("rest_start")
	private String restStart;

	@Schema(description = "休息结束时间")
	@TableField("rest_end")
	private String restEnd;

	@Schema(description = "休息开始规则 0 当日 1 次日")
	@TableField("rest_start_after")
	private Integer restStartAfter;

	@Schema(description = "休息结束规则 0 当日 1 次日")
	@TableField("rest_end_after")
	private Integer restEndAfter;

	@Schema(description = "加班起算时间（分钟）")
	private Integer overtime;

	@Schema(description = "工作时长展示")
	@TableField("work_time")
	private String workTime;

	@Schema(description = "颜色标识")
	private String color;

	@Schema(description = "排序")
	private Integer sort;

	@Schema(description = "创建人 admin.id")
	private Integer uid;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

	@TableLogic
	@Schema(description = "删除时间")
	private LocalDateTime deletedAt;

	@TableField(exist = false)
	@Schema(description = "列表：名称模糊")
	private String nameLike;

	@TableField(exist = false)
	@Schema(description = "列表：按考勤组过滤班次")
	private Integer groupFilterId;

}
