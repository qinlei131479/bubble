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
 * 考勤排班主表，对应 eb_attendance_arrange 表。
 *
 * @author qinlei
 * @date 2026/4/7 12:15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "考勤排班")
@TableName("eb_attendance_arrange")
public class AttendanceArrange extends Req<AttendanceArrange> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "考勤组ID")
	@TableField("group_id")
	private Integer groupId;

	@Schema(description = "创建人 admin.id")
	private Integer uid;

	@Schema(description = "排班月份锚点日期（PHP 存当月 1 号）")
	private LocalDateTime date;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

	@TableLogic
	@Schema(description = "删除时间")
	private LocalDateTime deletedAt;

	@TableField(exist = false)
	@Schema(description = "列表：名称模糊（考勤组名）")
	private String nameLike;

	@TableField(exist = false)
	@Schema(description = "列表：月份筛选 yyyy-MM 或 yyyy/MM-dd-yyyy/MM/dd")
	private String attendMonth;

}
