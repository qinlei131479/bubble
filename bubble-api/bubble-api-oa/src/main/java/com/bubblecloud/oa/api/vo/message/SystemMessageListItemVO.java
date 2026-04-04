package com.bubblecloud.oa.api.vo.message;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

/**
 * {@code GET ent/system/message/list} 单行（含 {@code message_template} 与分渠道模板字段）。
 *
 * @author qinlei
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SystemMessageListItemVO {

	private Long id;

	private Long entid;

	private Long relationId;

	private Long cateId;

	private Integer curlId;

	private String cateName;

	private String templateType;

	private Object templateVar;

	private Integer templateTime;

	private String title;

	private String content;

	private String remindTime;

	private Integer crudId;

	private Integer eventId;

	private Integer userSub;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	private LocalDateTime deletedAt;

	private List<MessageTemplateJsonVO> messageTemplate = new ArrayList<>();

	private MessageTemplateJsonVO systemTemplate;

	private MessageTemplateJsonVO smsTemplate;

	private MessageTemplateJsonVO workTemplate;

	private MessageTemplateJsonVO dingTemplate;

	private MessageTemplateJsonVO otherTemplate;

}
