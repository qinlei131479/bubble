package com.bubblecloud.oa.api.dto.hr;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 绩效考核评价 DTO（自评/上级评/审核评通用）。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Data
@Schema(description = "绩效考核评价")
public class AssessEvalDTO {

	@Schema(description = "评价内容")
	private String content;

	@Schema(description = "评分")
	private BigDecimal score;

}
