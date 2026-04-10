package com.bubblecloud.agi.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.bubblecloud.common.mybatis.base.Req;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 对话统计
 *
 * @author rampart
 * @date 2026-02-13 09:38:18
 */
@Data
@TableName("conversation_stats")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "对话统计")
public class ConversationStats extends Req<ConversationStats> {


	/**
	* 主键ID
	*/
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description="主键ID")
    private Long id;

	/**
	* 对话ID
	*/
    @Schema(description="对话ID")
    private Integer conversationId;

	/**
	* 消息总数
	*/
    @Schema(description="消息总数")
    private Integer messageCount;

	/**
	* 统计tokens
	*/
    @Schema(description="统计tokens")
    private Integer totalTokens;

	/**
	* 使用模型
	*/
    @Schema(description="使用模型")
    private String usedModel;

	/**
	* 用户反馈信息
	*/
    @Schema(description="用户反馈信息")
    private String userFeedback;

	/**
	* 创建时间
	*/
    @Schema(description="创建时间")
	@TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

	/**
	* 更新时间
	*/
    @Schema(description="更新时间")
	@TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}