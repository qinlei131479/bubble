package com.bubblecloud.oa.api.vo.hr;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 员工简要信息（绩效审核人等场景）。
 *
 * @author qinlei
 * @date 2026/4/7 11:00
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "员工简要信息")
public class AdminBriefVO {

	@Schema(description = "主键")
	private Long id;

	@Schema(description = "姓名")
	private String name;

	@Schema(description = "头像")
	private String avatar;

}
