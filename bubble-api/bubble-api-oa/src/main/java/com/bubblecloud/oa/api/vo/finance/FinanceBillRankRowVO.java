package com.bubblecloud.oa.api.vo.finance;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 财务占比分析一行（对齐 PHP {@code BillDao::getBillRank} + Statistics::calcRatio）。
 *
 * @author qinlei
 * @date 2026/4/8 15:00
 */
@Data
@Schema(description = "财务分类占比行")
public class FinanceBillRankRowVO {

	@JsonProperty("cate_id")
	@Schema(description = "分类ID")
	private Integer cateId;

	@Schema(description = "分类名称")
	private String name;

	@Schema(description = "金额合计")
	private BigDecimal sum;

	@Schema(description = "占比（百分比，两位小数）")
	private BigDecimal ratio;

}
