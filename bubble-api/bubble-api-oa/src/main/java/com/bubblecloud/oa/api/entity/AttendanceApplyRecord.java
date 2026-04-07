package com.bubblecloud.oa.api.entity;

import java.math.BigDecimal;
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
 * 考勤与审批关联记录，对应 eb_attendance_apply_record 表（请假/外出/出差/加班等核算）。
 *
 * @author qinlei
 * @date 2026/4/7 15:30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "考勤审批核算记录")
@TableName("eb_attendance_apply_record")
public class AttendanceApplyRecord extends Req<AttendanceApplyRecord> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Integer id;

	@Schema(description = "申请人 admin.id")
	private Integer uid;

	@Schema(description = "审批申请类型：1请假 2补卡 3加班 4外出 5出差")
	@TableField("apply_type")
	private Integer applyType;

	@TableField("type_unique")
	private String typeUnique;

	@TableField("date_type")
	private Integer dateType;

	@TableField("time_type")
	private String timeType;

	@TableField("calc_type")
	private Integer calcType;

	@TableField("work_hours")
	private BigDecimal workHours;

	@TableField("apply_id")
	private Integer applyId;

	private LocalDateTime startTime;

	private LocalDateTime endTime;

	@Schema(description = "扩展 JSON 字符串")
	private String others;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

}
