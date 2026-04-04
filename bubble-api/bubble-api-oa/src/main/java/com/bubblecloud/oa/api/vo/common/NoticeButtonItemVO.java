package com.bubblecloud.oa.api.vo.common;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

/**
 * 企业消息列表单条操作按钮（对齐 PHP NoticeRecordService::getButtonInfo）。
 * @author qinlei
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class NoticeButtonItemVO {

	private String url = "";

	private String uniUrl = "";

	private String action = "";

	private String title = "";

}
