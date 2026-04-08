package com.bubblecloud.oa.api.vo.program;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 动态分页列表（可选 total_count，对齐 PHP task 动态）。
 *
 * @author qinlei
 * @date 2026/4/8 11:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "动态列表")
public class ProgramDynamicListVO {

	@Schema(description = "列表")
	private List<ProgramDynamicRowVO> list = Collections.emptyList();

	@Schema(description = "当前页条数")
	private long count;

	@Schema(description = "同 relation_id 下任务动态总数（仅任务动态且 relation_id>0 时有值）")
	private Long totalCount;

}
