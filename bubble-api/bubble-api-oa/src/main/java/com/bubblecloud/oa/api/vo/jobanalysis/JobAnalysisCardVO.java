package com.bubblecloud.oa.api.vo.jobanalysis;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 工作分析详情中的员工名片摘要（对齐 PHP getInfo card）。
 *
 * @author qinlei
 * @date 2026/4/5 15:30
 */
@Data
@Schema(description = "工作分析详情名片")
public class JobAnalysisCardVO {

	@JsonProperty("id")
	private Long id;

	@JsonProperty("name")
	private String name;

	@JsonProperty("avatar")
	private String avatar;

	@JsonProperty("uid")
	private String uid;

	@JsonProperty("phone")
	private String phone;

}
