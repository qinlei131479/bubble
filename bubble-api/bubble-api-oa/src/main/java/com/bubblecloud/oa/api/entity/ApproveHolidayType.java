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
 * 审批假期类型，对应 eb_approve_holiday_type 表。
 *
 * @author qinlei
 * @date 2026/4/2 14:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "审批假期类型")
@TableName("eb_approve_holiday_type")
public class ApproveHolidayType extends Req<ApproveHolidayType> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "假期类型名称")
	private String name;

	@Schema(description = "新员工请假限制：0 不限制；1 限制")
	@TableField("new_employee_limit")
	private Integer newEmployeeLimit;

	@Schema(description = "新员工限制月数")
	@TableField("new_employee_limit_month")
	private Integer newEmployeeLimitMonth;

	@Schema(description = "时长类型：0 天；1 小时")
	@TableField("duration_type")
	private Integer durationType;

	@Schema(description = "时长计算：0 自然日；1 工作日")
	@TableField("duration_calc_type")
	private Integer durationCalcType;

	@Schema(description = "排序")
	private Integer sort;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

	@TableLogic
	@Schema(description = "删除时间")
	private LocalDateTime deletedAt;

	@TableField(exist = false)
	@Schema(description = "列表查询：名称模糊")
	private String nameLike;

}
