package com.bubblecloud.oa.api.vo.enterprise;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 当前企业详情（对齐 PHP {@code EnterpriseService::getEntAndUserInfo}）。
 *
 * @author qinlei
 * @date 2026/3/29 下午5:30
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "企业详情")
public class EnterpriseEntInfoVO {

	@JsonProperty("id")
	private Long id;

	@JsonProperty("logo")
	private String logo;

	@JsonProperty("title")
	private String title;

	@JsonProperty("enterprise_name")
	private String enterpriseName;

	@JsonProperty("enterprise_name_en")
	private String enterpriseNameEn;

	@JsonProperty("short_name")
	private String shortName;

	@JsonProperty("province")
	private String province;

	@JsonProperty("city")
	private String city;

	@JsonProperty("area")
	private String area;

	@JsonProperty("address")
	private String address;

	@JsonProperty("phone")
	private String phone;

	@JsonProperty("uid")
	private String uid;

	@JsonProperty("status")
	private Integer status;

	@JsonProperty("user")
	private EnterpriseOwnerUserVO user;

	@JsonProperty("frames")
	private Integer frames;

	@JsonProperty("enterprises")
	private Long enterprises;

}
