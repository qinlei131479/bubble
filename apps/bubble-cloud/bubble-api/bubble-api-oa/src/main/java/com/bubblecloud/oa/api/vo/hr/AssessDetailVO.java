package com.bubblecloud.oa.api.vo.hr;

import java.math.BigDecimal;
import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 绩效考核详情 VO。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Data
@Schema(description = "绩效考核详情")
public class AssessDetailVO {

	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "考核名称")
	private String name;

	@Schema(description = "被考核人ID")
	private Long userId;

	@Schema(description = "被考核人姓名")
	private String userName;

	@Schema(description = "考核人ID")
	private Long superiorId;

	@Schema(description = "考核人姓名")
	private String superiorName;

	@Schema(description = "审核人ID")
	private Long examineId;

	@Schema(description = "审核人姓名")
	private String examineName;

	@Schema(description = "考核开始日期")
	private LocalDate startDate;

	@Schema(description = "考核结束日期")
	private LocalDate endDate;

	@Schema(description = "自评分数")
	private BigDecimal selfScore;

	@Schema(description = "上级评分")
	private BigDecimal superiorScore;

	@Schema(description = "审核评分")
	private BigDecimal examineScore;

	@Schema(description = "最终得分")
	private BigDecimal finalScore;

	@Schema(description = "等级")
	private String level;

	@Schema(description = "状态")
	private Integer status;

	@Schema(description = "是否已申诉 0否 1是")
	private Integer isAppeal;

}
