package com.bubblecloud.oa.api.dto.hr;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 绩效申诉/驳回 DTO。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Data
@Schema(description = "绩效申诉/驳回")
public class AssessAppealDTO {

	@Schema(description = "申诉内容（申诉时必填）")
	private String content;

	@Schema(description = "处理结果：1通过 2驳回（审核时使用）")
	private Integer result;

	@Schema(description = "处理意见")
	private String opinion;

}
