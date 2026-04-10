package com.bubblecloud.oa.api.vo.program;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 管理员简要（任务关联展示）。
 *
 * @author qinlei
 * @date 2026/4/8 11:00
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "管理员简要")
public class ProgramAdminLiteVO {

	@Schema(description = "ID")
	private Long id;

	@Schema(description = "姓名")
	private String name;

	@Schema(description = "头像")
	private String avatar;

	@Schema(description = "用户UID")
	private String uid;

	@Schema(description = "手机")
	private String phone;

}
