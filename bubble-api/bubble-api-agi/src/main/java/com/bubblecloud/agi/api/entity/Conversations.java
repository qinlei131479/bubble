package com.bubblecloud.agi.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.bubblecloud.common.mybatis.base.Req;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 对话
 *
 * @author Rampart
 * @date 2026-02-13 09:36:42
 */
@Data
@TableName("conversations")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "对话")
public class Conversations extends Req<Conversations> {


	/**
	 * 主键ID
	 */
	@TableId(type = IdType.ASSIGN_ID)
	@Schema(description = "主键ID")
	private Long id;

	/**
	 * 线程ID (UUID)
	 */
	@Schema(description = "线程ID (UUID)")
	private String threadId;

	/**
	 * 用户ID
	 */
	@Schema(description = "用户ID")
	private String userId;

	/**
	 * 智能体ID
	 */
	@Schema(description = "智能体ID")
	private String agentId;

	/**
	 * 对话标题
	 */
	@Schema(description = "对话标题")
	private String title;

	/**
	 * 状态: active/archived/deleted
	 */
	@Schema(description = "状态: active/archived/deleted")
	private String status;

	/**
	 * 元数据扩展信息
	 */
	@Schema(description = "元数据扩展信息")
	private String extMetadata;

	/**
	 * 创建时间
	 */
	@Schema(description = "创建时间")
	@TableField(fill = FieldFill.INSERT)
	private LocalDateTime createdAt;

	/**
	 * 更新时间
	 */
	@Schema(description = "更新时间")
	@TableField(fill = FieldFill.INSERT_UPDATE)
	private LocalDateTime updatedAt;


}