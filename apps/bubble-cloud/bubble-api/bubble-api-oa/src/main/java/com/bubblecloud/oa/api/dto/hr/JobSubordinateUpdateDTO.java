package com.bubblecloud.oa.api.dto.hr;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 修改下级职责 DTO。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Data
@Schema(description = "修改下级职责")
public class JobSubordinateUpdateDTO {

	@Schema(description = "职责内容")
	private String duty;

	@Schema(description = "描述")
	private String describe;

}
