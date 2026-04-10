package com.bubblecloud.oa.api.dto.hr;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 海氏评估组保存 DTO。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Data
@Schema(description = "海氏评估组保存")
public class HayGroupSaveDTO {

	@Schema(description = "评估组名称")
	private String name;

	@Schema(description = "备注")
	private String mark;

	@Schema(description = "状态 1启用 0停用")
	private Integer status;

}
