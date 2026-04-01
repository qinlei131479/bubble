package com.bubblecloud.oa.api.dto.hr;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 绩效指标自评 DTO。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Data
@Schema(description = "绩效指标自评")
public class AssessTargetEvalDTO {

	@Schema(description = "绩效考核ID")
	private Long assessId;

	@Schema(description = "指标ID")
	private Long targetId;

	@Schema(description = "自评内容")
	private String content;

	@Schema(description = "自评分数")
	private BigDecimal score;

}
