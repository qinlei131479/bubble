package com.bubblecloud.biz.oa.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;

import cn.hutool.core.util.ObjectUtil;
import lombok.Data;

/**
 * 绩效考核周期时间窗（对齐 PHP {@code AssessService::abnormalList} 的周期切分）。
 *
 * @author qinlei
 * @date 2026/4/7 12:30
 */
@Data
public class AssessPeriodWindow {

	private LocalDateTime start;

	private LocalDateTime end;

	private String title;

	/**
	 * 根据周期类型与锚点时间计算窗口。
	 */
	public static AssessPeriodWindow of(Integer period, LocalDateTime anchor) {
		LocalDateTime t = ObjectUtil.defaultIfNull(anchor, LocalDateTime.now());
		LocalDate d = t.toLocalDate();
		AssessPeriodWindow w = new AssessPeriodWindow();
		if (ObjectUtil.isNull(period)) {
			throw new IllegalArgumentException("无效的考核周期");
		}
		switch (period) {
			case 1 -> {
				LocalDate monday = d.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
				LocalDate sunday = monday.plusDays(6);
				w.start = LocalDateTime.of(monday, LocalTime.MIN);
				w.end = LocalDateTime.of(sunday, LocalTime.MAX);
				int week = d.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
				w.title = d.getYear() + "年第" + week + "周考核";
			}
			case 2 -> {
				w.start = d.with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
				w.end = d.with(TemporalAdjusters.lastDayOfMonth()).atTime(LocalTime.MAX);
				w.title = d.getYear() + "年" + d.getMonthValue() + "月考核";
			}
			case 3 -> {
				w.start = d.with(TemporalAdjusters.firstDayOfYear()).atStartOfDay();
				w.end = d.with(TemporalAdjusters.lastDayOfYear()).atTime(LocalTime.MAX);
				w.title = d.getYear() + "年考核";
			}
			case 4 -> {
				if (d.getMonthValue() > 6) {
					w.start = d.withMonth(7).with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
					w.end = d.with(TemporalAdjusters.lastDayOfYear()).atTime(LocalTime.MAX);
					w.title = d.getYear() + "年下半年考核";
				}
				else {
					w.start = d.with(TemporalAdjusters.firstDayOfYear()).atStartOfDay();
					w.end = d.withMonth(6).with(TemporalAdjusters.lastDayOfMonth()).atTime(LocalTime.MAX);
					w.title = d.getYear() + "年上半年考核";
				}
			}
			case 5 -> {
				int m = d.getMonthValue();
				int q = (m - 1) / 3 + 1;
				int firstMonth = (q - 1) * 3 + 1;
				LocalDate qs = d.withMonth(firstMonth).with(TemporalAdjusters.firstDayOfMonth());
				LocalDate qe = qs.plusMonths(3).minusDays(1);
				w.start = qs.atStartOfDay();
				w.end = LocalDateTime.of(qe, LocalTime.MAX);
				w.title = d.getYear() + "年第" + q + "季度考核";
			}
			default -> throw new IllegalArgumentException("无效的考核周期");
		}
		return w;
	}

}
