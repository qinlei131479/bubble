package com.bubblecloud.oa.api.vo.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 客户审批规则（工作台合同弹窗）。
 *
 * @author qinlei
 * @date 2026/3/29 下午4:00
 */
@Data
@Schema(description = "客户审批规则")
public class ClientRuleApproveVO {

	@Schema(description = "合同退款开关")
	private Integer contractRefundSwitch;

	@Schema(description = "合同续费开关")
	private Integer contractRenewSwitch;

}
