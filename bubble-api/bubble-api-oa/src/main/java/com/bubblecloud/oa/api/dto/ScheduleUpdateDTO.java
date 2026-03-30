package com.bubblecloud.oa.api.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * PUT /ent/schedule/update/{id}（在 {@link ScheduleSaveDTO} 基础上增加修改方式）。
 *
 * @author qinlei
 * @date 2026/3/29 20:40
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "修改日程")
public class ScheduleUpdateDTO extends ScheduleSaveDTO {

	@Schema(description = "0 仅当前 1 当前及以后 2 全部（Java 仅完整实现 2）")
	private Integer type;

	private String start;

	private String end;

}
