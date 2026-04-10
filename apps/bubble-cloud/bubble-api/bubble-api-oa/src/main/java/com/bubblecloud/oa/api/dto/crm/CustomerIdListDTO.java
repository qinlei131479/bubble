package com.bubblecloud.oa.api.dto.crm;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 批量客户 ID 请求体（对齐 PHP postMore data 数组）。
 *
 * @author qinlei
 * @date 2026/4/3 11:00
 */
@Data
@Schema(description = "客户ID列表")
public class CustomerIdListDTO {

	@Schema(description = "客户ID列表")
	private List<Long> data;

	@Schema(description = "退回原因")
	private String reason;

}
