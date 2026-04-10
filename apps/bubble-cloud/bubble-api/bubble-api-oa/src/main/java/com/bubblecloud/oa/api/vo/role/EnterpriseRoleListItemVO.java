package com.bubblecloud.oa.api.vo.role;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 角色列表行（蛇形字段，对齐 PHP getRolesList + frame）。
 *
 * @author qinlei
 * @date 2026/4/5 18:00
 */
@Data
@Schema(description = "企业角色列表行")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnterpriseRoleListItemVO {

	@JsonProperty("id")
	private Long id;

	@JsonProperty("role_name")
	private String roleName;

	@JsonProperty("user_count")
	private Integer userCount;

	@JsonProperty("status")
	private Integer status;

	@JsonProperty("data_level")
	private Integer dataLevel;

	@JsonProperty("directly")
	private Integer directly;

	@JsonProperty("frame_id")
	private String frameId;

	@JsonProperty("frame")
	private List<FrameBriefVO> frame = Collections.emptyList();

	@Data
	@Schema(description = "部门摘要")
	public static class FrameBriefVO {

		@JsonProperty("id")
		private Long id;

		@JsonProperty("name")
		private String name;

	}

}
