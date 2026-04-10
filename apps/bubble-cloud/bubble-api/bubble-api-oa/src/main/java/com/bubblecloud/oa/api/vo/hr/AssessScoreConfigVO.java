package com.bubblecloud.oa.api.vo.hr;

import java.util.List;

import com.bubblecloud.oa.api.entity.AssessScore;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 绩效积分/计分配置（对齐 PHP {@code getScoreConfig}）。
 *
 * @author qinlei
 * @date 2026/4/7 11:00
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "绩效评分配置")
public class AssessScoreConfigVO {

	@Schema(description = "计分模式")
	private Integer computeMode;

	@Schema(description = "评分说明文案")
	private String scoreMark;

	@Schema(description = "等级区间列表")
	private List<AssessScore> scoreList;

}
