package com.bubblecloud.oa.api.vo.hr;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 未创建考核的人员项（对齐 PHP {@code abnormalList} 列表元素核心字段）。
 *
 * @author qinlei
 * @date 2026/4/7 12:00
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "绩效未创建人员")
public class AssessAbnormalUserVO {

	@Schema(description = "员工ID")
	private Long id;

	@Schema(description = "姓名")
	private String name;

	@Schema(description = "头像")
	private String avatar;

	@Schema(description = "岗位ID")
	private Integer job;

	@Schema(description = "考核周期标题")
	private String title;

}
