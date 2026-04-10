package com.bubblecloud.oa.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 单日日历配置项。
 *
 * @author qinlei
 * @date 2026/4/2 14:00
 */
@Data
@Schema(description = "日历单日项")
public class CalendarDayItemDTO {

	@Schema(description = "日期 YYYY-MM-DD")
	private String day;

	@Schema(description = "是否休息：0 上班；1 休息")
	private Integer isRest;

}
