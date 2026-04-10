package com.bubblecloud.oa.api.dto.attendance;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 单人整月班次数组（对齐 PHP updateArrange 中单条 data）。
 *
 * @author qinlei
 * @date 2026/4/7 12:15
 */
@Data
@Schema(description = "排班更新-单人数据")
public class AttendanceArrangeUserShiftRowDTO {

	@Schema(description = "人员 admin.id")
	private Integer uid;

	@Schema(description = "按日班次 ID，长度须等于当月天数")
	private List<Integer> shifts;

}
