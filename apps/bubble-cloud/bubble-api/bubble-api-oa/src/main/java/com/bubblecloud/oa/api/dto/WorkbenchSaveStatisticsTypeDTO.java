package com.bubblecloud.oa.api.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * POST /ent/user/work/statistics_type。
 *
 * @author qinlei
 * @date 2026/3/29 下午5:00
 */
@Data
@Schema(description = "保存业绩统计类型")
public class WorkbenchSaveStatisticsTypeDTO {

	@Schema(description = "类型 key 列表")
	private List<String> data;

}
