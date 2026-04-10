package com.bubblecloud.oa.api.vo.program;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 项目任务统计（对齐 PHP task_statistics）。
 *
 * @author qinlei
 * @date 2026/4/6 15:30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "项目任务统计")
public class ProgramTaskStatisticsVO {

	@Schema(description = "任务总数")
	private long total;

	@Schema(description = "未完成（0+1）")
	private long incomplete;

}
