package com.bubblecloud.oa.api.dto.attendance;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 排班批量更新请求体（对齐 PHP update：date + data）。
 *
 * @author qinlei
 * @date 2026/4/7 12:15
 */
@Data
@Schema(description = "排班批量更新")
public class AttendanceArrangeBatchUpdateDTO {

	@Schema(description = "目标月份 yyyy-MM")
	private String date;

	@Schema(description = "人员排班数据")
	private List<AttendanceArrangeUserShiftRowDTO> data;

}
