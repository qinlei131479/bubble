package com.bubblecloud.oa.api.vo.message;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 系统消息列表分页结果（蛇形 JSON）。
 *
 * @author qinlei
 * @date 2026/4/5
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "系统消息列表结果")
public class SystemMessageListResultVO {

	@Schema(description = "列表数据")
	private List<SystemMessageListItemVO> list = Collections.emptyList();

	@Schema(description = "总条数")
	private long count;

}
