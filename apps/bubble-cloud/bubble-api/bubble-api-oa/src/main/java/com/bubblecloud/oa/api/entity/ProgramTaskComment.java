package com.bubblecloud.oa.api.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bubblecloud.common.mybatis.base.Req;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 项目任务评论，对应 eb_program_task_comment 表。
 *
 * @author qinlei
 * @date 2026/4/8 10:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "任务评论")
@TableName("eb_program_task_comment")
public class ProgramTaskComment extends Req<ProgramTaskComment> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "任务ID")
	private Long taskId;

	@Schema(description = "父评论ID")
	private Long pid;

	@Schema(description = "被回复人ID")
	private Long replyUid;

	@Schema(description = "评论人ID")
	private Long uid;

	@Schema(description = "内容")
	private String describe;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

	@Schema(description = "删除时间")
	@TableLogic
	private LocalDateTime deletedAt;

}
