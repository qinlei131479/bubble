package com.bubblecloud.oa.api.dto;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

/**
 * {@code PUT/DELETE ent/company/message/batch} 请求体（蛇形字段对齐 PHP）。
 * @author qinlei
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CompanyMessageBatchDTO {

	private List<Long> ids = Collections.emptyList();

	private Long cateId;

}
