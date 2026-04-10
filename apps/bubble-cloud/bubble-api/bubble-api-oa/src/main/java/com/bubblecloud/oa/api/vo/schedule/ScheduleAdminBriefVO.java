package com.bubblecloud.oa.api.vo.schedule;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 日程关联用户简要信息（对齐 PHP admin 字段）。
 *
 * @author qinlei
 * @date 2026/3/29 20:45
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ScheduleAdminBriefVO {

	private Long id;

	private String name;

	private String avatar;

	private String phone;

}
