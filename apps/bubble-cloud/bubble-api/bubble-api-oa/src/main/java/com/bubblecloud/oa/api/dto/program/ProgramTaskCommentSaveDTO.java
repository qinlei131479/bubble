package com.bubblecloud.oa.api.dto.program;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 任务评论保存/修改。
 *
 * @author qinlei
 * @date 2026/4/8 10:30
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "任务评论参数")
public class ProgramTaskCommentSaveDTO {

	@Schema(description = "父评论ID")
	private Long pid;

	@Schema(description = "任务ID")
	private Long taskId;

	@Schema(description = "内容")
	private String describe;

	@Schema(description = "被回复人ID")
	private Long replyUid;

}
