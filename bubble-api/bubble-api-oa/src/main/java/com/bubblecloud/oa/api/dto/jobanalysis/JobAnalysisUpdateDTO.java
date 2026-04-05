package com.bubblecloud.oa.api.dto.jobanalysis;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 工作分析保存（对齐 PHP {@code EnterpriseUserJobAnalysisRequest}）。
 *
 * @author qinlei
 * @date 2026/4/5 15:30
 */
@Data
@Schema(description = "工作分析更新请求")
public class JobAnalysisUpdateDTO {

	@NotBlank(message = "请填写分析内容")
	@Schema(description = "分析内容（HTML）")
	private String data;

}
