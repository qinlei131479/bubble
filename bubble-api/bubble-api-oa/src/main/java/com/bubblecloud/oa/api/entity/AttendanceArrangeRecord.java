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
 * 考勤排班明细，对应 eb_attendance_arrange_record 表。
 *
 * @author qinlei
 * @date 2026/4/7 12:15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "考勤排班记录")
@TableName("eb_attendance_arrange_record")
public class AttendanceArrangeRecord extends Req<AttendanceArrangeRecord> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "排班主表ID")
	@TableField("arrange_id")
	private Long arrangeId;

	@Schema(description = "考勤组ID")
	@TableField("group_id")
	private Integer groupId;

	@Schema(description = "人员 admin.id")
	private Integer uid;

	@Schema(description = "班次ID")
	@TableField("shift_id")
	private Integer shiftId;

	@Schema(description = "排班日期")
	private LocalDateTime date;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

	@TableLogic
	@Schema(description = "删除时间")
	private LocalDateTime deletedAt;

	@TableField(exist = false)
	@Schema(description = "按月份查询 yyyy-MM")
	private String queryMonth;

	@TableField(exist = false)
	@Schema(description = "日期大于（不含）用于清理未来排班")
	private LocalDateTime dateAfter;

}
