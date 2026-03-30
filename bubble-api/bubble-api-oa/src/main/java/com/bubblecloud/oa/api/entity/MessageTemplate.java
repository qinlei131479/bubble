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
 * 消息渠道模板，对应 eb_message_template。
 *
 * @author qinlei
 * @date 2026/3/29 20:30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "消息模板")
@TableName("eb_message_template")
public class MessageTemplate extends Req<MessageTemplate> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	private Long id;

	@TableField("relation_id")
	private Integer relationId;

	@TableField("message_id")
	private Long messageId;

	private Integer type;

	@TableField("template_id")
	private String templateId;

	@TableField("message_title")
	private String messageTitle;

	private String image;

	private String url;

	@TableField("uni_url")
	private String uniUrl;

	private Integer status;

	@TableField("webhook_url")
	private String webhookUrl;

	@TableField("relation_status")
	private Integer relationStatus;

	@TableField("content_template")
	private String contentTemplate;

	@TableField("button_template")
	private String buttonTemplate;

	@TableField("push_rule")
	private Integer pushRule;

	private Integer minute;

	@TableField("crud_event_id")
	private Integer crudEventId;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	@TableField("deleted_at")
	private LocalDateTime deletedAt;

}
