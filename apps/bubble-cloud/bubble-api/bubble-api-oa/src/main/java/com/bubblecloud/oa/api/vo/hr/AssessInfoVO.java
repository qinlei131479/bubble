package com.bubblecloud.oa.api.vo.hr;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 绩效考核详情（对齐 PHP {@code getAssessInfo} 的 assessInfo + info + explain）。
 *
 * @author qinlei
 * @date 2026/4/7 11:00
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "绩效考核详情载荷")
public class AssessInfoVO {

	@Schema(description = "考核主表信息（含 level 等展示字段）")
	private JsonNode assessInfo;

	@Schema(description = "考核维度/指标结构（对齐 PHP AssessSpace，占位可为空对象）")
	private JsonNode info;

	@Schema(description = "评分说明文案")
	private String explain;

}
