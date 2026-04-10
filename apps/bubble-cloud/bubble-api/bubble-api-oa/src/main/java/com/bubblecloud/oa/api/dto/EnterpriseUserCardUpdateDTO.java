package com.bubblecloud.oa.api.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * PUT /ent/user/card/{id}（对齐 PHP EnterpriseUserCardRequest）。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@Data
public class EnterpriseUserCardUpdateDTO {

	@NotEmpty(message = "必须选择一个部门")
	@JsonProperty("frame_id")
	private List<Integer> frameId;

	@NotNull(message = "必须选择一个主部门")
	@JsonProperty("mastart_id")
	private Integer mastartId;

	private String name;

	/** 岗位 ID（对应 eb_admin.job） */
	private String position;

	private String phone;

	@JsonProperty("is_admin")
	private Integer isAdmin;

	@JsonProperty("superior_uid")
	private Long superiorUid;

	@JsonProperty("manage_frames")
	private List<Integer> manageFrames;

}
