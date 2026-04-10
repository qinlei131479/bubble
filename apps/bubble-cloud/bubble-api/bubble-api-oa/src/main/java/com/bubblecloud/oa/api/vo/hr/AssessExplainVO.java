package com.bubblecloud.oa.api.vo.hr;

import java.util.List;

import com.bubblecloud.oa.api.entity.AssessReply;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 绩效其他信息（对齐 PHP {@code getAssessExplain}）。
 *
 * @author qinlei
 * @date 2026/4/7 11:00
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "绩效评价/申诉说明")
public class AssessExplainVO {

	@Schema(description = "评价记录")
	private List<AssessReply> reply;

	@Schema(description = "评分说明")
	private String explain;

	@Schema(description = "申诉记录")
	private List<AssessReply> appeal;

	@Schema(description = "上级可见备注")
	private List<AssessReply> mark;

}
