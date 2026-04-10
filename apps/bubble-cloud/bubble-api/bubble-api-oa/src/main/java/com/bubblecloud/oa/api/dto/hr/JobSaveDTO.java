package com.bubblecloud.oa.api.dto.hr;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 岗位保存 DTO。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Data
@Schema(description = "岗位保存")
public class JobSaveDTO {

	@Schema(description = "岗位名称")
	private String name;

	@Schema(description = "岗位描述")
	private String describe;

	@Schema(description = "岗位职责")
	private String duty;

	@Schema(description = "状态 1启用 0停用")
	private Integer status;

}
