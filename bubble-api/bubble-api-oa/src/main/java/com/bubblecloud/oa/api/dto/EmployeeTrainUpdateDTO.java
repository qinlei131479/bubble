package com.bubblecloud.oa.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 更新培训内容。
 *
 * @author qinlei
 * @date 2026/3/29 下午6:30
 */
@Data
@Schema(description = "培训内容更新")
public class EmployeeTrainUpdateDTO {

	@Schema(description = "HTML 内容")
	private String content;

	@Schema(description = "类型")
	private String type;
}
