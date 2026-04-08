package com.bubblecloud.oa.api.vo.program;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 任务评论树节点。
 *
 * @author qinlei
 * @date 2026/4/8 11:00
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "评论树节点")
public class ProgramCommentNodeVO {

	@Schema(description = "主键")
	private Long id;

	@Schema(description = "任务ID")
	private Long taskId;

	@Schema(description = "父评论ID")
	private Long pid;

	@Schema(description = "回复对象用户ID")
	private Long replyUid;

	@Schema(description = "评论人ID")
	private Long uid;

	@Schema(description = "内容")
	private String describe;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "评论人")
	private ProgramAdminLiteVO member;

	@Schema(description = "被回复人")
	private ProgramAdminLiteVO replyMember;

	@Schema(description = "子评论")
	private List<ProgramCommentNodeVO> children = new ArrayList<>();

}
