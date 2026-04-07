package com.bubblecloud.oa.api.vo.hr;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 绩效考核统计 VO（折线 census 与柱状 census_bar 共用，字段按场景填充）。
 *
 * @author qinlei
 * @date 2026/4/7 11:00
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "绩效考核统计")
public class AssessCensusVO {

	@Schema(description = "各等级人数统计（简化统计场景）")
	private List<AssessCensusItemVO> levelStats;

	@Schema(description = "总人数")
	private Integer total;

	@Schema(description = "折线图 series（对齐 PHP getAssessCensusLine）")
	private JsonNode series;

	@Schema(description = "折线图横轴")
	private List<String> xAxis;

	@Schema(description = "柱状图等级分布（对齐 PHP getAssessStatistics.score）")
	private JsonNode score;

	@Schema(description = "部门信息")
	private JsonNode frame;

	@Schema(description = "柱状图合计")
	private Integer count;

	/**
	 * 等级统计项。
	 */
	@Data
	@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
	public static class AssessCensusItemVO {

		@Schema(description = "等级名称")
		private String level;

		@Schema(description = "人数")
		private Integer count;

		@Schema(description = "占比(%)")
		private Double ratio;

	}

}
