package com.bubblecloud.biz.oa.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import cn.hutool.core.util.StrUtil;

/**
 * CRM 统计环比时间区间与环比比例（对齐 PHP {@code crmeb\\utils\\Date::ringRatioTime}、
 * {@code Statistics::ringRatio}）。
 *
 * @author qinlei
 * @date 2026/4/8 16:30
 */
public final class CrmRingRatioUtil {

	private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

	private CrmRingRatioUtil() {
	}

	/**
	 * @param originTime 形如 {@code 2023/01/01 00:00:00-2023/01/31 23:59:59}；空则当月首日
	 * 00:00:00 至月末 23:59:59
	 * @return [0]=当前统计区间（与 PHP 一致，结束为 endOfDay），[1]=环比对比区间
	 */
	public static String[] ringRatioTime(String originTime) {
		ZoneId z = ZoneId.systemDefault();
		String range = originTime;
		if (StrUtil.isBlank(range)) {
			LocalDate today = LocalDate.now(z);
			LocalDateTime start = today.withDayOfMonth(1).atStartOfDay();
			LocalDateTime end = today.withDayOfMonth(today.lengthOfMonth()).atTime(23, 59, 59);
			range = FMT.format(start) + "-" + FMT.format(end);
		}
		String[] parts = range.split("-", 2);
		if (parts.length != 2) {
			return new String[] { range, range };
		}
		LocalDateTime originStart = LocalDateTime.parse(parts[0].trim(), FMT);
		LocalDateTime originEntParsed = LocalDateTime.parse(parts[1].trim(), FMT);
		String t0 = parts[0].trim();
		String t1 = parts[1].trim();
		long day;
		if (t0.equals(t1)) {
			day = 1;
		}
		else {
			long d = ChronoUnit.DAYS.between(originStart.toLocalDate(), originEntParsed.toLocalDate());
			if (d < 0) {
				return new String[] { range, range };
			}
			day = d;
		}
		LocalDateTime searchEnd = originEntParsed.toLocalDate().atTime(23, 59, 59);
		String search = FMT.format(originStart) + "-" + FMT.format(searchEnd);
		LocalDateTime ratioStart = originStart.minusDays(day + 1);
		LocalDateTime ratioEnd = originEntParsed.minusDays(day + 1).toLocalDate().atTime(23, 59, 59);
		String ratio = FMT.format(ratioStart) + "-" + FMT.format(ratioEnd);
		return new String[] { search, ratio };
	}

	/**
	 * 环比百分比（对齐 PHP {@code Statistics::ringRatio}）。
	 */
	public static int ringRatio(BigDecimal first, BigDecimal second) {
		BigDecimal f = first == null ? BigDecimal.ZERO : first;
		BigDecimal s = second == null ? BigDecimal.ZERO : second;
		if (f.compareTo(s) == 0) {
			return 0;
		}
		if (s.compareTo(BigDecimal.ZERO) == 0) {
			return 100;
		}
		return f.subtract(s).divide(s, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).intValue();
	}

	public static int ringRatio(long first, long second) {
		return ringRatio(BigDecimal.valueOf(first), BigDecimal.valueOf(second));
	}

}
