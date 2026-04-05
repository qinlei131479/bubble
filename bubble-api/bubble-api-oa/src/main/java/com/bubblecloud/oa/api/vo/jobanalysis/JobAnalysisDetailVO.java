package com.bubblecloud.oa.api.vo.jobanalysis;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 工作分析详情（对齐 PHP getInfo 有记录时的结构）。
 *
 * @author qinlei
 * @date 2026/4/5 15:30
 */
@Data
@Schema(description = "工作分析详情")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JobAnalysisDetailVO {

	@JsonProperty("uid")
	@Schema(description = "员工 admin 主键")
	private Long uid;

	@JsonProperty("data")
	@Schema(description = "分析 HTML")
	private String data;

	@JsonProperty("created_at")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdAt;

	@JsonProperty("updated_at")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updatedAt;

	@JsonProperty("card")
	private JobAnalysisCardVO card;

}
