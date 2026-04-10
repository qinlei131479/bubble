package com.bubblecloud.oa.api.vo.crm;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 合同分类维度聚合（对齐 PHP ContractService::getCategoryRank 汇总字段，名称由 contract_category 原值展示）。
 *
 * @author qinlei
 * @date 2026/4/8 16:45
 */
@Data
@Schema(description = "CRM 合同分类聚合行")
public class CrmContractCategoryAggVO implements Serializable {

	private static final long serialVersionUID = 1L;

	@Schema(description = "合同分类 JSON 串（eb_contract.contract_category）")
	private String contractCategory;

	@Schema(description = "收入侧金额（账单 types 0,1）")
	private BigDecimal price;

	@Schema(description = "合同数（types=0 去重）")
	private Long contractCount;

	@Schema(description = "支出侧金额（账单 types=2）")
	private BigDecimal expend;

}
