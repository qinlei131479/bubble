package com.bubblecloud.oa.api.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * GET /ent/user/schedule 查询参数。
 *
 * @author qinlei
 * @date 2026/3/29 20:40
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "用户待办日程查询")
public class UserScheduleQueryDTO {

	@Schema(description = "企业 id")
	private Integer entid = 1;

	@Schema(description = "范围开始日期 yyyy-MM-dd")
	private String start;

	@Schema(description = "范围结束日期 yyyy-MM-dd")
	private String end;

}
