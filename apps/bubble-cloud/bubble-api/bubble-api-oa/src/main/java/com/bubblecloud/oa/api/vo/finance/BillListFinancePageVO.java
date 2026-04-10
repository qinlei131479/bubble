package com.bubblecloud.oa.api.vo.finance;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import com.bubblecloud.oa.api.entity.BillList;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 财务流水分页结果（对齐 PHP BillService::getList 的 compact）。
 *
 * @author qinlei
 * @date 2026/4/6 15:30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "财务流水分页")
public class BillListFinancePageVO {

	@Schema(description = "列表")
	private List<BillList> list = Collections.emptyList();

	@Schema(description = "总条数")
	private long count;

	@Schema(description = "收入合计")
	private BigDecimal income = BigDecimal.ZERO;

	@Schema(description = "支出合计")
	private BigDecimal expend = BigDecimal.ZERO;

}
