package com.bubblecloud.oa.api.dto.attendance;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 考勤组第二步：地点。
 *
 * @author qinlei
 * @date 2026/4/2 16:00
 */
@Data
@Schema(description = "考勤组第二步地点")
public class AttendanceGroupStepTwoDTO {

	@Schema(description = "详细地址")
	private String address;

	@Schema(description = "纬度")
	private String lat;

	@Schema(description = "经度")
	private String lng;

	@Schema(description = "有效范围")
	private Integer effectiveRange;

	@Schema(description = "地点名称")
	private String locationName;

}
