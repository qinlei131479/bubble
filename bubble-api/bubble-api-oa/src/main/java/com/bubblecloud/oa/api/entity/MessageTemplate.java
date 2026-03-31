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
 * 消息渠道模板，对应 eb_message_template 表。
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
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "关联业务ID")
	@TableField("relation_id")
	private Integer relationId;

	@Schema(description = "消息主表ID")
	@TableField("message_id")
	private Long messageId;

	@Schema(description = "类型")
	private Integer type;

	@Schema(description = "第三方模板ID")
	@TableField("template_id")
	private String templateId;

	@Schema(description = "模板标题")
	@TableField("message_title")
	private String messageTitle;

	@Schema(description = "配图")
	private String image;

	@Schema(description = "链接")
	private String url;

	@Schema(description = "小程序链接")
	@TableField("uni_url")
	private String uniUrl;

	@Schema(description = "状态")
	private Integer status;

	@Schema(description = "Webhook 地址")
	@TableField("webhook_url")
	private String webhookUrl;

	@Schema(description = "关联状态")
	@TableField("relation_status")
	private Integer relationStatus;

	@Schema(description = "内容模板")
	@TableField("content_template")
	private String contentTemplate;

	@Schema(description = "按钮模板")
	@TableField("button_template")
	private String buttonTemplate;

	@Schema(description = "推送规则")
	@TableField("push_rule")
	private Integer pushRule;

	@Schema(description = "提前分钟数")
	private Integer minute;

	@Schema(description = "低代码事件ID")
	@TableField("crud_event_id")
	private Integer crudEventId;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

	@Schema(description = "删除时间")
	@TableField("deleted_at")
	private LocalDateTime deletedAt;

}
