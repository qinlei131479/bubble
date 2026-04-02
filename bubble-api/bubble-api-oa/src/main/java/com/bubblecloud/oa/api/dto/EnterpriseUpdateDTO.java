package com.bubblecloud.oa.api.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 修改当前企业信息（对齐 PHP {@code PUT ent/company/info}）。
 *
 * @author qinlei
 * @date 2026/3/29 下午5:30
 */
@Data
@Schema(description = "企业信息更新")
public class EnterpriseUpdateDTO {

	@Schema(description = "Logo")
	private String logo;

	@JsonAlias("enterprise_name")
	@Schema(description = "企业名称")
	private String enterpriseName;

	@Schema(description = "省")
	private String province;

	@Schema(description = "市")
	private String city;

	@Schema(description = "区")
	private String area;

	@Schema(description = "详细地址")
	private String address;

	@Schema(description = "手机号")
	private String phone;

	@JsonAlias("short_name")
	@Schema(description = "简称")
	private String shortName;

}
