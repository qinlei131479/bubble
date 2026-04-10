package com.bubblecloud.oa.api.vo.attendance;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 班次列表创建人简要（对齐 PHP card 关联）。
 *
 * @author qinlei
 * @date 2026/4/7 12:15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "班次创建人卡片")
public class AttendanceShiftAdminBriefVO {

	@JsonProperty("id")
	@Schema(description = "admin.id")
	private Long id;

	@JsonProperty("name")
	private String name;

	@JsonProperty("avatar")
	private String avatar;

	@JsonProperty("phone")
	private String phone;

}
