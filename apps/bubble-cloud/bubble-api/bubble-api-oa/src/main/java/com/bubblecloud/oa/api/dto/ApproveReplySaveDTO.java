package com.bubblecloud.oa.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 审批评价保存。
 *
 * @author qinlei
 * @date 2026/4/2 14:00
 */
@Data
@Schema(description = "审批评价保存参数")
public class ApproveReplySaveDTO {

	@Schema(description = "申请ID")
	private Long applyId;

	@Schema(description = "内容")
	private String content;

}
