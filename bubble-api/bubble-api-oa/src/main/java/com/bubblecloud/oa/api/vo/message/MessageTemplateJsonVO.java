package com.bubblecloud.oa.api.vo.message;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 系统消息列表中的模板行（蛇形 JSON，对齐 Laravel toArray）。
 *
 * @author qinlei
 * @date 2026/4/5
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "消息模板 JSON 行")
public class MessageTemplateJsonVO {

	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "关联业务ID")
	private Integer relationId;

	@Schema(description = "消息ID")
	private Long messageId;

	@Schema(description = "类型")
	private Integer type;

	@Schema(description = "模板标识")
	private String templateId;

	@Schema(description = "消息标题模板")
	private String messageTitle;

	@Schema(description = "图片")
	private String image;

	@Schema(description = "链接URL")
	private String url;

	@Schema(description = "站内链接URL")
	private String uniUrl;

	@Schema(description = "状态")
	private Integer status;

	@Schema(description = "Webhook地址")
	private String webhookUrl;

	@Schema(description = "关联状态")
	private Integer relationStatus;

	@Schema(description = "内容模板")
	private String contentTemplate;

	@Schema(description = "按钮模板")
	private String buttonTemplate;

	@Schema(description = "推送规则")
	private Integer pushRule;

	@Schema(description = "分钟偏移")
	private Integer minute;

	@Schema(description = "CRUD 事件ID")
	private Integer crudEventId;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

	@Schema(description = "删除时间")
	private LocalDateTime deletedAt;

}
