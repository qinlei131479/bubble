package com.bubblecloud.oa.api.vo.company;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 组织架构成员卡片（对齐 PHP {@code editAdminFrame} 精简字段，后续可扩展）。
 *
 * @author qinlei
 * @date 2026/3/29 下午5:30
 */
@Data
@Schema(description = "组织架构成员卡片")
public class CompanyUserCardVO {

	@JsonProperty("id")
	private Long id;

	@JsonProperty("uid")
	private String uid;

	@JsonProperty("name")
	private String name;

	@JsonProperty("phone")
	private String phone;

	@JsonProperty("avatar")
	private String avatar;

	@JsonProperty("job")
	private Integer job;

}
