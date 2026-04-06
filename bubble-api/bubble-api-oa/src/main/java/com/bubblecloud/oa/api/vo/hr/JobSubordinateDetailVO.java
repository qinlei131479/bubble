package com.bubblecloud.oa.api.vo.hr;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 下级职责详情（对齐 PHP {@code getSubordinateInfo}）。
 *
 * @author qinlei
 * @date 2026/4/5 18:30
 */
@Data
@Schema(description = "下级职责详情")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class JobSubordinateDetailVO {

	@Schema(description = "员工 id")
	private Long id;

	@Schema(description = "姓名")
	private String name;

	@Schema(description = "职责内容")
	private String duty;

}
