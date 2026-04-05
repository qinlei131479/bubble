package com.bubblecloud.oa.api.vo.message;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * {@code GET ent/system/message/list} 单行（含 {@code message_template} 与分渠道模板字段）。
 *
 * @author qinlei
 * @date 2026/4/5
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "系统消息列表项")
public class SystemMessageListItemVO {

	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "企业ID")
	private Long entid;

	@Schema(description = "关联业务ID")
	private Long relationId;

	@Schema(description = "分类ID")
	private Long cateId;

	@Schema(description = "渠道ID")
	private Integer curlId;

	@Schema(description = "分类名称")
	private String cateName;

	@Schema(description = "模板类型")
	private String templateType;

	@Schema(description = "模板变量")
	private Object templateVar;

	@Schema(description = "模板时间")
	private Integer templateTime;

	@Schema(description = "标题")
	private String title;

	@Schema(description = "内容")
	private String content;

	@Schema(description = "提醒时间")
	private String remindTime;

	@Schema(description = "CRUD ID")
	private Integer crudId;

	@Schema(description = "事件ID")
	private Integer eventId;

	@Schema(description = "用户订阅标识")
	private Integer userSub;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

	@Schema(description = "删除时间")
	private LocalDateTime deletedAt;

	@Schema(description = "消息模板列表")
	private List<MessageTemplateJsonVO> messageTemplate = new ArrayList<>();

	@Schema(description = "系统渠道模板")
	private MessageTemplateJsonVO systemTemplate;

	@Schema(description = "短信渠道模板")
	private MessageTemplateJsonVO smsTemplate;

	@Schema(description = "企业微信渠道模板")
	private MessageTemplateJsonVO workTemplate;

	@Schema(description = "钉钉渠道模板")
	private MessageTemplateJsonVO dingTemplate;

	@Schema(description = "其他渠道模板")
	private MessageTemplateJsonVO otherTemplate;

}
