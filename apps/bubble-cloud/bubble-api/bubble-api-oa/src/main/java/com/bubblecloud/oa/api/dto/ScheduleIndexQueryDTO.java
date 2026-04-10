package com.bubblecloud.oa.api.dto;

import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * POST /ent/schedule/index 请求体（对齐 PHP：start_time、end_time、cid、period）。
 *
 * @author qinlei
 * @date 2026/3/29 20:40
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "日程列表查询")
public class ScheduleIndexQueryDTO {

	@Schema(description = "范围开始时间")
	private String startTime;

	@Schema(description = "范围结束时间")
	private String endTime;

	@Schema(description = "日程类型 id 列表，空表示全部")
	private List<Long> cid;

	@Schema(description = "日历视图：1 日 2 周 3 月（Java 侧仅用于后续扩展重复展开）")
	private Integer period;

}
