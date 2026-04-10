package com.bubblecloud.oa.api.vo.placeholder;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

/**
 * 假期类型下拉项（占位）。
 *
 * @author qinlei
 * @date 2026/3/29 下午5:00
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class HolidayTypeOptionVO {

	private Object value;

	private String label;

	private String durationType;

}
