package com.bubblecloud.oa.api.vo.hr;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 绩效评分记录 VO。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Data
@Schema(description = "绩效评分记录")
public class AssessScoreRecordVO {

	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "等级名称")
	private String name;

	@Schema(description = "分数最小值")
	private Integer min;

	@Schema(description = "分数最大值")
	private Integer max;

	@Schema(description = "级别")
	private Integer level;

	@Schema(description = "说明")
	private String mark;

}
