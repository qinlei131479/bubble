package com.bubblecloud.oa.api.dto.finance;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 企业支付方式保存 DTO。
 *
 * @author qinlei
 * @date 2026/4/6 15:30
 */
@Data
@Schema(description = "企业支付方式保存")
public class EnterprisePaytypeSaveDTO {

	@Schema(description = "名称")
	private String name;

	@Schema(description = "标识")
	private String ident;

	@Schema(description = "状态")
	private Integer status;

	@Schema(description = "简介")
	private String info;

	@Schema(description = "企业ID")
	private Integer entid;

	@Schema(description = "排序")
	private Integer sort;

	@Schema(description = "系统支付方式 type_id")
	private Integer typeId;

}
