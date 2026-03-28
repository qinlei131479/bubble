package com.bubblecloud.oa.api.vo;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 站点配置响应（兼容 PHP 前端蛇形字段命名）。
 */
@Data
@Schema(description = "站点配置")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SiteVO {

	@Schema(description = "备案号")
	private String siteRecordNumber;

	@Schema(description = "企业地址")
	private String siteAddress;

	@Schema(description = "企业电话")
	private String siteTel;

	@Schema(description = "企业Logo")
	private String siteLogo;

	@Schema(description = "密码类型编码（如 02 表示数字+字母）")
	private String passwordType;

	@Schema(description = "密码最小长度")
	private Integer passwordLength;

	@Schema(description = "系统版本号")
	private String versionName;

	@Schema(description = "附件大小限制（MB）")
	private Integer globalAttachSize;

}
