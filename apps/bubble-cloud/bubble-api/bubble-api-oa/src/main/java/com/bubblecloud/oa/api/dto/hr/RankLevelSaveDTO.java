package com.bubblecloud.oa.api.dto.hr;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 职位等级保存 DTO。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Data
@Schema(description = "职位等级保存")
public class RankLevelSaveDTO {

	@Schema(description = "关联职位ID")
	private Long jobId;

	@Schema(description = "等级名称")
	private String name;

	@Schema(description = "薪资下限")
	private BigDecimal salaryMin;

	@Schema(description = "薪资上限")
	private BigDecimal salaryMax;

	@Schema(description = "排序")
	private Integer sort;

	@Schema(description = "状态 1启用 0停用")
	private Integer status;

}
