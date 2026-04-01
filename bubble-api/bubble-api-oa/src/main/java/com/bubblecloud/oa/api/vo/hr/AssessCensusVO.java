package com.bubblecloud.oa.api.vo.hr;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 绩效考核统计 VO。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Data
@Schema(description = "绩效考核统计")
public class AssessCensusVO {

	@Schema(description = "各等级人数统计")
	private List<AssessCensusItemVO> levelStats;

	@Schema(description = "总人数")
	private Integer total;

	/**
	 * 等级统计项。
	 */
	@Data
	public static class AssessCensusItemVO {

		@Schema(description = "等级名称")
		private String level;

		@Schema(description = "人数")
		private Integer count;

		@Schema(description = "占比(%)")
		private Double ratio;

	}

}
