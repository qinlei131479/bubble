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
 * 考勤班次规则，对应 eb_attendance_shift_rule 表。
 *
 * @author qinlei
 * @date 2026/4/7 12:15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "考勤班次规则")
@TableName("eb_attendance_shift_rule")
public class AttendanceShiftRule extends Req<AttendanceShiftRule> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Integer id;

	@Schema(description = "班次ID")
	@TableField("shift_id")
	private Integer shiftId;

	@Schema(description = "第几次上下班 1/2")
	private Integer number;

	@Schema(description = "上班是否次日 0/1")
	@TableField("first_day_after")
	private Integer firstDayAfter;

	@Schema(description = "下班是否次日 0/1")
	@TableField("second_day_after")
	private Integer secondDayAfter;

	@Schema(description = "上班时间 HH:mm")
	@TableField("work_hours")
	private String workHours;

	@Schema(description = "迟到阈值（秒）")
	private Integer late;

	@Schema(description = "严重迟到阈值（秒）")
	@TableField("extreme_late")
	private Integer extremeLate;

	@Schema(description = "晚到缺卡阈值（秒）")
	@TableField("late_lack_card")
	private Integer lateLackCard;

	@Schema(description = "提前打卡（秒）")
	@TableField("early_card")
	private Integer earlyCard;

	@Schema(description = "下班时间 HH:mm")
	@TableField("off_hours")
	private String offHours;

	@Schema(description = "早退阈值（秒）")
	@TableField("early_leave")
	private Integer earlyLeave;

	@Schema(description = "提前缺卡阈值（秒）")
	@TableField("early_lack_card")
	private Integer earlyLackCard;

	@Schema(description = "延后打卡（秒）")
	@TableField("delay_card")
	private Integer delayCard;

	@Schema(description = "下班可免打卡 0/1")
	@TableField("free_clock")
	private Integer freeClock;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

	@TableLogic
	@Schema(description = "删除时间")
	private LocalDateTime deletedAt;

}
