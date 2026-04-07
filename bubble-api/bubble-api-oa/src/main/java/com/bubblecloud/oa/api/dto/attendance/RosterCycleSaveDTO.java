package com.bubblecloud.oa.api.dto.attendance;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 排班周期保存/修改（shifts 兼容整型数组或含 shift_id 的对象数组）。
 *
 * @author qinlei
 * @date 2026/4/7 12:15
 */
@Data
@Schema(description = "排班周期保存参数")
public class RosterCycleSaveDTO {

	@Schema(description = "考勤组ID")
	private Integer groupId;

	@Schema(description = "周期名称")
	private String name;

	@Schema(description = "周期天数")
	private Integer cycle;

	@Schema(description = "班次序列")
	private JsonNode shifts;

}
