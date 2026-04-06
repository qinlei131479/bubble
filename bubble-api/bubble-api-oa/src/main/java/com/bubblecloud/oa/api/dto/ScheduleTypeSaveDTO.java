package com.bubblecloud.oa.api.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 日程类型保存/更新字段。
 *
 * @author qinlei
 * @date 2026/4/6 10:00
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "日程类型保存")
public class ScheduleTypeSaveDTO {

	private String name;

	private String color;

	private String info;

}
