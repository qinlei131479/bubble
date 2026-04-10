package com.bubblecloud.oa.api.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * PUT /ent/schedule/status/{id} 请求体。
 *
 * @author qinlei
 * @date 2026/3/29 20:40
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "日程状态更新")
public class ScheduleStatusUpdateDTO {

	@Schema(description = "日程实例ID")
	private Long id;

	@Schema(description = "状态：0 待定 1 接受 2 拒绝 3 完成")
	private Integer status;

	@Schema(description = "任务实例开始时间")
	private String start;

	@Schema(description = "任务实例结束时间")
	private String end;

}
