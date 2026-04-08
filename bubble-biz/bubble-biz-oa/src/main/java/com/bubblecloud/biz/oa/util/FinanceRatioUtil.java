package com.bubblecloud.biz.oa.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.bubblecloud.oa.api.vo.finance.FinanceBillRankRowVO;

import cn.hutool.core.collection.CollUtil;

/**
 * 财务占比计算（对齐 PHP {@code crmeb\\utils\\Statistics::calcRatio}）。
 *
 * @author qinlei
 * @date 2026/4/8 15:00
 */
public final class FinanceRatioUtil {

	private FinanceRatioUtil() {
	}

	/**
	 * @param rows 待计算行（会按 sum 降序排序）
	 * @param total 分母；为 0 时按各行 sum 之和
	 */
	public static List<FinanceBillRankRowVO> calcRatio(List<FinanceBillRankRowVO> rows, BigDecimal total) {
		if (CollUtil.isEmpty(rows)) {
			return rows;
		}
		List<FinanceBillRankRowVO> sorted = new ArrayList<>(rows);
		sorted.sort(Comparator.comparing(FinanceBillRankRowVO::getSum, Comparator.nullsFirst(BigDecimal::compareTo))
			.reversed());
		BigDecimal sumTotal = total;
		if (sumTotal == null || sumTotal.compareTo(BigDecimal.ZERO) == 0) {
			sumTotal = BigDecimal.ZERO;
			for (FinanceBillRankRowVO r : sorted) {
				if (r.getSum() != null) {
					sumTotal = sumTotal.add(r.getSum());
				}
			}
		}
		if (sumTotal.compareTo(BigDecimal.ZERO) == 0) {
			for (FinanceBillRankRowVO r : sorted) {
				r.setRatio(BigDecimal.ZERO);
			}
			return sorted;
		}
		BigDecimal ratioAcc = BigDecimal.ZERO;
		int n = sorted.size();
		for (int i = 0; i < n; i++) {
			FinanceBillRankRowVO row = sorted.get(i);
			BigDecimal part = row.getSum() == null ? BigDecimal.ZERO : row.getSum();
			if (i == n - 1) {
				row.setRatio(new BigDecimal("100").subtract(ratioAcc).setScale(2, RoundingMode.HALF_UP));
			}
			else {
				BigDecimal ratio = part.divide(sumTotal, 4, RoundingMode.HALF_UP)
					.multiply(new BigDecimal("100"))
					.setScale(2, RoundingMode.HALF_UP);
				row.setRatio(ratio);
				ratioAcc = ratioAcc.add(ratio);
			}
		}
		return sorted;
	}

}
