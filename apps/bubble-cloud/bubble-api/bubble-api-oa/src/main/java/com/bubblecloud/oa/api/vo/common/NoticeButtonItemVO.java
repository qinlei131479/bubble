package com.bubblecloud.oa.api.vo.common;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 企业消息列表单条操作按钮（对齐 PHP NoticeRecordService::getButtonInfo）。
 *
 * @author qinlei
 * @date 2026/4/5
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "消息操作按钮项")
public class NoticeButtonItemVO {

	@Schema(description = "链接URL")
	private String url = "";

	@Schema(description = "站内链接URL")
	private String uniUrl = "";

	@Schema(description = "动作标识")
	private String action = "";

	@Schema(description = "按钮标题")
	private String title = "";

}
