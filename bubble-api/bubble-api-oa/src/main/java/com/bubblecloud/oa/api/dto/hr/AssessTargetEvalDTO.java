package com.bubblecloud.oa.api.dto.hr;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 绩效指标自评（对齐 PHP {@code evalTarget} 参数）。
 *
 * @author qinlei
 * @date 2026/4/7 11:00
 */
@Data
@Schema(description = "绩效指标自评")
public class AssessTargetEvalDTO {

	@Schema(description = "考核ID（PHP assess_id）")
	private Long assessId;

	@Schema(description = "指标ID")
	private Long targetId;

	@Schema(description = "维度/空间ID")
	private Long spaceId;

	@Schema(description = "完成情况说明")
	private String finishInfo;

	@Schema(description = "完成比例")
	private BigDecimal finishRatio;

}
