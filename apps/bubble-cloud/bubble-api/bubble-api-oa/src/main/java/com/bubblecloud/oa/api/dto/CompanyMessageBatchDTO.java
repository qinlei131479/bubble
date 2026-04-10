package com.bubblecloud.oa.api.dto;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * {@code PUT/DELETE ent/company/message/batch} 请求体（蛇形字段对齐 PHP）。
 *
 * @author qinlei
 * @date 2026/4/5
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "企业消息批量操作请求")
public class CompanyMessageBatchDTO {

	@Schema(description = "消息ID列表")
	private List<Long> ids = Collections.emptyList();

	@Schema(description = "分类ID")
	private Long cateId;

}
