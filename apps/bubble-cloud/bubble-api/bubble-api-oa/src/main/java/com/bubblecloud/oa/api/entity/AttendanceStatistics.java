package com.bubblecloud.oa.api.entity;

import java.math.BigDecimal;
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
 * 考勤日统计，对应 eb_attendance_statistics 表。
 *
 * @author qinlei
 * @date 2026/4/7
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "考勤日统计")
@TableName("eb_attendance_statistics")
public class AttendanceStatistics extends Req<AttendanceStatistics> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "考勤人员 admin.id")
	private Integer uid;

	@Schema(description = "部门ID")
	@TableField("frame_id")
	private Integer frameId;

	@Schema(description = "考勤组ID")
	@TableField("group_id")
	private Integer groupId;

	@Schema(description = "考勤组名称")
	@TableField("`group`")
	private String group;

	@Schema(description = "班次ID")
	@TableField("shift_id")
	private Integer shiftId;

	@Schema(description = "班次数据 JSON")
	@TableField("shift_data")
	private String shiftData;

	@TableField("one_shift_time")
	private LocalDateTime oneShiftTime;

	@TableField("one_shift_is_after")
	private Integer oneShiftIsAfter;

	@TableField("one_shift_status")
	private Integer oneShiftStatus;

	@TableField("one_shift_location_status")
	private Integer oneShiftLocationStatus;

	@TableField("one_shift_record_id")
	private Long oneShiftRecordId;

	@TableField("two_shift_time")
	private LocalDateTime twoShiftTime;

	@TableField("two_shift_is_after")
	private Integer twoShiftIsAfter;

	@TableField("two_shift_status")
	private Integer twoShiftStatus;

	@TableField("two_shift_location_status")
	private Integer twoShiftLocationStatus;

	@TableField("two_shift_record_id")
	private Long twoShiftRecordId;

	@TableField("three_shift_time")
	private LocalDateTime threeShiftTime;

	@TableField("three_shift_is_after")
	private Integer threeShiftIsAfter;

	@TableField("three_shift_status")
	private Integer threeShiftStatus;

	@TableField("three_shift_location_status")
	private Integer threeShiftLocationStatus;

	@TableField("three_shift_record_id")
	private Long threeShiftRecordId;

	@TableField("four_shift_time")
	private LocalDateTime fourShiftTime;

	@TableField("four_shift_is_after")
	private Integer fourShiftIsAfter;

	@TableField("four_shift_status")
	private Integer fourShiftStatus;

	@TableField("four_shift_location_status")
	private Integer fourShiftLocationStatus;

	@TableField("four_shift_record_id")
	private Long fourShiftRecordId;

	@TableField("required_work_hours")
	private BigDecimal requiredWorkHours;

	@TableField("actual_work_hours")
	private BigDecimal actualWorkHours;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	@TableLogic
	private LocalDateTime deletedAt;

}
