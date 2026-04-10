package com.bubblecloud.oa.api.vo.hr;

import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 绩效审核配置（对齐 PHP {@code getVerifyConfig}）。
 *
 * @author qinlei
 * @date 2026/4/7 11:00
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "绩效审核配置")
public class AssessVerifyConfigVO {

	@Schema(description = "是否上级审核")
	private String isSuperior;

	@Schema(description = "是否指定人员")
	private String isAppoint;

	@Schema(description = "指定人员列表")
	private List<AdminBriefVO> staff;

}
