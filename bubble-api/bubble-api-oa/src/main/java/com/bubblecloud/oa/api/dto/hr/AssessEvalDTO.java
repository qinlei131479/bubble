package com.bubblecloud.oa.api.dto.hr;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 绩效考核评价 DTO（自评/上级评/审核评，对齐 PHP postMore）。
 *
 * @author qinlei
 * @date 2026/4/7 11:00
 */
@Data
@Schema(description = "绩效考核评价")
public class AssessEvalDTO {

	@Schema(description = "考核指标/维度数据（PHP data）")
	private JsonNode data;

	@Schema(description = "是否提交（PHP is_submit）")
	private Integer isSubmit;

	@Schema(description = "自评备注（PHP mark → self_reply）")
	private String mark;

	@Schema(description = "上级评价备注（PHP mark → reply）")
	private String replyMark;

	@Schema(description = "上级隐藏备注（PHP hide_mark）")
	private String hideMark;

	@Schema(description = "直接提交的分数（无空间表时的兜底）")
	private BigDecimal score;

}
