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
 * 考勤打卡记录，对应 eb_attendance_clock_record 表。
 *
 * @author qinlei
 * @date 2026/4/7
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "考勤打卡记录")
@TableName("eb_attendance_clock_record")
public class AttendanceClockRecord extends Req<AttendanceClockRecord> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	private Long id;

	@TableField("frame_id")
	private Integer frameId;

	@TableField("group_id")
	private Integer groupId;

	@TableField("`group`")
	private String group;

	@TableField("shift_id")
	private Integer shiftId;

	@TableField("shift_data")
	private String shiftData;

	private String address;

	private String lat;

	private String lng;

	private String remark;

	private String image;

	private Integer uid;

	@TableField("is_external")
	private Integer isExternal;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	@TableLogic
	private LocalDateTime deletedAt;

}
