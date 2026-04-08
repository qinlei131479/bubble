package com.bubblecloud.oa.api.vo.crm;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 业务员业绩排行一行（对齐 PHP ClientBillDao::getRankList 聚合字段）。
 *
 * @author qinlei
 * @date 2026/4/8 16:45
 */
@Data
@Schema(description = "CRM 业务员排行行")
public class CrmBillRankRowVO implements Serializable {

	private static final long serialVersionUID = 1L;

	@Schema(description = "业务员 admin id（字符串，与账单 uid 一致）")
	private String uid;

	@Schema(description = "业务员姓名")
	private String name;

	@Schema(description = "收入合计（合同+续费 types 0,1）")
	private BigDecimal price;

	@Schema(description = "支出合计 types=2")
	private BigDecimal expend;

}
