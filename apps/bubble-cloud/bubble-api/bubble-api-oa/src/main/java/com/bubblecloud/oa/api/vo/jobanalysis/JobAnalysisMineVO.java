package com.bubblecloud.oa.api.vo.jobanalysis;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 我的工作分析（对齐 PHP getInfoByUid）。
 *
 * @author qinlei
 * @date 2026/4/5 15:30
 */
@Data
@Schema(description = "我的工作分析")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JobAnalysisMineVO {

	@JsonProperty("uid")
	private Long uid;

	@JsonProperty("data")
	private String data;

	@JsonProperty("created_at")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdAt;

	@JsonProperty("updated_at")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updatedAt;

}
