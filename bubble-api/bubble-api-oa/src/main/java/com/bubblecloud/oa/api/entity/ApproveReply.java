package com.bubblecloud.oa.api.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bubblecloud.common.mybatis.base.Req;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 审批申请评价，对应 eb_approve_reply 表。
 *
 * @author qinlei
 * @date 2026/4/2 14:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "审批申请评价")
@TableName("eb_approve_reply")
public class ApproveReply extends Req<ApproveReply> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "用户 admin.id")
	@TableField("user_id")
	private Long userId;

	@Schema(description = "名片ID")
	@TableField("card_id")
	private Long cardId;

	@Schema(description = "申请ID")
	@TableField("apply_id")
	private Long applyId;

	@Schema(description = "回复内容")
	private String content;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

}
