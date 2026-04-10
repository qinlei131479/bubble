package com.bubblecloud.oa.api.vo.attendance;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 考勤组下拉项。
 *
 * @author qinlei
 * @date 2026/4/2 16:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "考勤组下拉")
public class AttendanceGroupSelectItemVO {

	@Schema(description = "考勤组ID")
	private Integer id;

	@Schema(description = "考勤组名称")
	private String name;

}
