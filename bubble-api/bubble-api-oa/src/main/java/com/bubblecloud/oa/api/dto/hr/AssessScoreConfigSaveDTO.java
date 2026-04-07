package com.bubblecloud.oa.api.dto.hr;

import java.util.List;

import com.bubblecloud.oa.api.entity.AssessScore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 保存绩效评分配置（对齐 PHP {@code saveScore} 请求体）。
 *
 * @author qinlei
 * @date 2026/4/7 11:00
 */
@Data
@Schema(description = "绩效评分配置保存")
public class AssessScoreConfigSaveDTO {

	@Schema(description = "评分说明")
	private String scoreMark;

	@Schema(description = "计分模式")
	private Integer computeMode;

	@Schema(description = "等级区间列表")
	private List<AssessScore> scoreList;

}
