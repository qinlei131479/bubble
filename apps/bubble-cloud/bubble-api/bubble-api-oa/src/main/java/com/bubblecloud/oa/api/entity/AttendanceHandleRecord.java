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
 * 考勤状态变更记录，对应 eb_attendance_handle_record 表。
 *
 * @author qinlei
 * @date 2026/4/7
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "考勤处理记录")
@TableName("eb_attendance_handle_record")
public class AttendanceHandleRecord extends Req<AttendanceHandleRecord> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	private Integer id;

	@TableField("statistics_id")
	private Long statisticsId;

	@TableField("shift_number")
	private Integer shiftNumber;

	@TableField("before_status")
	private Integer beforeStatus;

	@TableField("before_location_status")
	private Integer beforeLocationStatus;

	@TableField("after_status")
	private Integer afterStatus;

	@TableField("after_location_status")
	private Integer afterLocationStatus;

	private String result;

	private String remark;

	private Integer source;

	private Integer uid;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

}
