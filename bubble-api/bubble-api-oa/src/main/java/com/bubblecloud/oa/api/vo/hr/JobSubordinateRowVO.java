package com.bubblecloud.oa.api.vo.hr;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 下级岗位职责列表行（对齐 PHP {@code subordinate} 列表字段，operate 待与架构权限联动）。
 *
 * @author qinlei
 * @date 2026/4/5 18:30
 */
@Data
@Schema(description = "下级岗位职责行")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class JobSubordinateRowVO {

	@Schema(description = "员工 id")
	private Long id;

	@Schema(description = "姓名")
	private String name;

	@Schema(description = "岗位 id")
	private Integer job;

	@Schema(description = "手机号")
	private String phone;

	@Schema(description = "岗位名称")
	private String jobName;

	@Schema(description = "架构名称")
	private String frameNames;

	@Schema(description = "是否可操作（下级）")
	private Boolean operate;

}
