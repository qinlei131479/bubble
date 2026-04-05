package com.bubblecloud.oa.api.vo.common;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * GET /ent/common/message 单条结构：蛇形字段 + {@code buttons} + 解析后的 JSON 字段。
 *
 * @author qinlei
 * @date 2026/4/5
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "站内信单条")
public class CommonMessageItemVO {

	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "发送方ID")
	private Integer sendId;

	@Schema(description = "接收方UID")
	private String toUid;

	@Schema(description = "链接URL")
	private String url = "";

	@Schema(description = "站内链接URL")
	private String uniUrl = "";

	@Schema(description = "图片")
	private String image;

	@Schema(description = "标题")
	private String title;

	@Schema(description = "消息正文")
	private String message;

	@Schema(description = "消息类型")
	private Integer type;

	@Schema(description = "分类ID")
	private Integer cateId;

	@Schema(description = "消息模板ID")
	private Integer messageId;

	@Schema(description = "分类名称")
	private String cateName;

	@Schema(description = "是否已读")
	private Integer isRead;

	@Schema(description = "是否已处理")
	private Integer isHandle;

	@Schema(description = "是否展示")
	private Integer isShow;

	@Schema(description = "模板类型")
	private String templateType;

	@Schema(description = "按钮模板")
	private Object buttonTemplate;

	@Schema(description = "扩展字段")
	private Object other;

	@Schema(description = "关联业务ID")
	private Integer linkId;

	@Schema(description = "关联状态")
	private Integer linkStatus;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

	@Schema(description = "操作按钮列表")
	private List<NoticeButtonItemVO> buttons = Collections.emptyList();

	@Schema(description = "详情列表（无 is_handle 时为空）")
	private List<?> detail = Collections.emptyList();

	@Schema(description = "企业信息（可选）")
	private Map<String, Object> enterprise;

	@Schema(description = "用户信息（可选）")
	private Map<String, Object> user;

	@Schema(description = "消息模板关联（可选）")
	private List<Object> messageTemplate = Collections.emptyList();

}
