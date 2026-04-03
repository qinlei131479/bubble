package com.bubblecloud.oa.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 假期类型保存/修改。
 *
 * @author qinlei
 * @date 2026/4/2 14:00
 */
@Data
@Schema(description = "假期类型保存参数")
public class ApproveHolidayTypeSaveDTO {

	@Schema(description = "名称")
	private String name;

	@Schema(description = "新员工限制")
	private Integer newEmployeeLimit;

	@Schema(description = "新员工限制月数")
	private Integer newEmployeeLimitMonth;

	@Schema(description = "时长类型")
	private Integer durationType;

	@Schema(description = "时长计算类型")
	private Integer durationCalcType;

	@Schema(description = "排序")
	private Integer sort;

}
