package com.bubblecloud.oa.api.vo.role;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 角色成员行（对齐 PHP getRoleUser 列表项：含主部门 frame.name）。
 *
 * @author qinlei
 * @date 2026/4/5 18:00
 */
@Data
@Schema(description = "角色成员行")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoleMemberRowVO {

	@JsonProperty("id")
	private Long id;

	@JsonProperty("name")
	private String name;

	@JsonProperty("frame")
	private FrameNameVO frame;

	@JsonProperty("status")
	private Integer status;

	@Data
	public static class FrameNameVO {

		@JsonProperty("name")
		private String name;

	}

}
