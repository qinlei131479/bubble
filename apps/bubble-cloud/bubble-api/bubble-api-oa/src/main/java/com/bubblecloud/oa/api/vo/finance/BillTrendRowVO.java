package com.bubblecloud.oa.api.vo.finance;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 财务流水按时间粒度聚合的一行（对齐 PHP {@code BillDao::getTrend} 行）。
 *
 * @author qinlei
 * @date 2026/4/8 15:00
 */
@Data
@Schema(description = "财务趋势聚合行")
public class BillTrendRowVO {

	@Schema(description = "时间桶标签，与 DATE_FORMAT 一致")
	private String days;

	@Schema(description = "金额合计")
	private BigDecimal num;

}
