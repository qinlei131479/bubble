package com.bubblecloud.oa.api.vo.common;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

/**
 * GET /ent/common/message 单条结构：蛇形字段 + {@code buttons} + 解析后的 JSON 字段。
 * @author qinlei
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CommonMessageItemVO {

	private Long id;

	private Integer sendId;

	private String toUid;

	private String url = "";

	private String uniUrl = "";

	private String image;

	private String title;

	private String message;

	private Integer type;

	private Integer cateId;

	private Integer messageId;

	private String cateName;

	private Integer isRead;

	private Integer isHandle;

	private Integer isShow;

	private String templateType;

	private Object buttonTemplate;

	private Object other;

	private Integer linkId;

	private Integer linkStatus;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	private List<NoticeButtonItemVO> buttons = Collections.emptyList();

	/** PHP 在 is_handle 为空时为 []；common 接口不传 is_handle，恒为空列表 */
	private List<?> detail = Collections.emptyList();

	/** 企业信息（可选，与 PHP with enterprise 一致） */
	private Map<String, Object> enterprise;

	/** 用户（可选，与 PHP with user 一致：id、name） */
	private Map<String, Object> user;

	/**
	 * 企业消息记录页会对 {@code message_template} 做 sort；站内信无此关联时返回空数组避免前端报错。
	 */
	private List<Object> messageTemplate = Collections.emptyList();

}
