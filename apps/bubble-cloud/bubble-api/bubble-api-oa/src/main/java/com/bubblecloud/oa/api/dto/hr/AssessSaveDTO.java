package com.bubblecloud.oa.api.dto.hr;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 绩效考核保存 DTO（与 {@code eb_assess} 常用字段对齐，复杂结构由后续 AssessSpace 扩展）。
 *
 * @author qinlei
 * @date 2026/4/7 12:00
 */
@Data
@Schema(description = "绩效考核保存")
public class AssessSaveDTO {

	@Schema(description = "企业ID")
	private Long entid;

	@Schema(description = "考核名称")
	private String name;

	@Schema(description = "周期 1-5")
	private Integer period;

	@Schema(description = "计划ID planid")
	private Long planId;

	@Schema(description = "组织 frame_id")
	private Integer frameId;

	@Schema(description = "被考核人 test_uid")
	private Long testUid;

	@Schema(description = "上级 check_uid")
	private Long checkUid;

	@Schema(description = "评分方式 types")
	private Integer types;

	@Schema(description = "目标制定状态 make_status")
	private Integer makeStatus;

	@Schema(description = "流程状态 status")
	private Integer status;

	@Schema(description = "是否启用 is_show")
	private Integer isShow;

	@Schema(description = "满分 total")
	private BigDecimal assessTotal;

	@Schema(description = "得分 score")
	private BigDecimal assessScore;

	@Schema(description = "等级 grade")
	private Integer assessGrade;

}
