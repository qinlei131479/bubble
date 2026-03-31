package com.bubblecloud.oa.api.vo.config;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 客户审批相关开关配置（合同退款/续费/支出/开票/作废）。
 *
 * @author qinlei
 * @date 2026/3/30 下午11:05
 */
@Data
@Schema(description = "客户审批规则配置")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ClientRuleApproveConfigVO {

	@Schema(description = "合同退款开关")
	private Integer contractRefundSwitch;

	@Schema(description = "合同续费开关")
	private Integer contractRenewSwitch;

	@Schema(description = "合同支出开关")
	private Integer contractDisburseSwitch;

	@Schema(description = "开票开关")
	private Integer invoicingSwitch;

	@Schema(description = "作废发票开关")
	private Integer voidInvoiceSwitch;

}
