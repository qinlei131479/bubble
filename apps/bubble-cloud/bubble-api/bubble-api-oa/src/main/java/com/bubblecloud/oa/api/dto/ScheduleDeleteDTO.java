package com.bubblecloud.oa.api.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DELETE /ent/schedule/delete/{id} 请求体。
 *
 * @author qinlei
 * @date 2026/4/6 10:00
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "删除日程")
public class ScheduleDeleteDTO {

	@Schema(description = "0 当前 1 当前及以后 2 全部")
	private Integer type;

	private String start;

	private String end;

}
