package com.bubblecloud.oa.api.vo.message;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

/**
 * 系统消息列表中的模板行（蛇形 JSON，对齐 Laravel toArray）。
 * @author qinlei
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MessageTemplateJsonVO {

	private Long id;

	private Integer relationId;

	private Long messageId;

	private Integer type;

	private String templateId;

	private String messageTitle;

	private String image;

	private String url;

	private String uniUrl;

	private Integer status;

	private String webhookUrl;

	private Integer relationStatus;

	private String contentTemplate;

	private String buttonTemplate;

	private Integer pushRule;

	private Integer minute;

	private Integer crudEventId;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	private LocalDateTime deletedAt;

}
