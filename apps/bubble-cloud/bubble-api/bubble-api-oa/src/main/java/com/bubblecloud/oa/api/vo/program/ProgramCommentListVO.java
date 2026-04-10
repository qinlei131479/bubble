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
 * 评论列表（含 total_count，对齐 PHP）。
 *
 * @author qinlei
 * @date 2026/4/8 11:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "评论列表包装")
public class ProgramCommentListVO {

	@Schema(description = "树形列表")
	private List<ProgramCommentNodeVO> list = Collections.emptyList();

	@Schema(description = "该任务评论总数")
	private long totalCount;

}
