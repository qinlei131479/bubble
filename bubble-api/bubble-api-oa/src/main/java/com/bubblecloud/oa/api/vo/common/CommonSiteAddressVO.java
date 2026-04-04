package com.bubblecloud.oa.api.vo.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GET /ent/common/site_address 站点网址与展示信息。
 *
 * @author qinlei
 * @date 2026/4/4 16:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "站点网址信息")
public class CommonSiteAddressVO {

	@Schema(description = "站点根地址")
	private String address;

	@Schema(description = "AI 配图等配置")
	private String aiImage;

	@Schema(description = "默认 Logo")
	private String logo;

	@Schema(description = "站点名称")
	private String siteName;

}
