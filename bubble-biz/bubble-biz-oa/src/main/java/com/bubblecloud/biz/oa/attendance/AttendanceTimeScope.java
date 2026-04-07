package com.bubblecloud.biz.oa.attendance;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;
import java.util.regex.Pattern;

import cn.hutool.core.util.StrUtil;

/**
 * 对齐 PHP {@code TimeDataTrait::scopeTime} 对 {@code created_at} 的筛选语义（企业时区
 * {@link AttendanceShiftRuleValidation#zone()}）。
 *
 * @author qinlei
 * @date 2026/4/7
 */
public final class AttendanceTimeScope {

	private static final ZoneId TZ = AttendanceShiftRuleValidation.zone();

	private static final Pattern LATELY = Pattern.compile("^lately([1-9]\\d{0,2})$");

	private AttendanceTimeScope() {
	}

	public record Range(LocalDateTime startInclusive, LocalDateTime endInclusive) {
	}

	/**
	 * @param timeValue 请求参数 time：today、month、自定义区间等
	 */
	public static Range resolveCreatedRange(String timeValue) {
		String v = StrUtil.trimToEmpty(timeValue);
		if (StrUtil.isBlank(v)) {
			v = "today";
		}
		LocalDate today = LocalDate.now(TZ);
		return switch (v) {
			case "today" -> dayRange(today);
			case "yesterday" -> dayRange(today.minusDays(1));
			case "week" -> new Range(today.with(java.time.DayOfWeek.MONDAY).atStartOfDay(),
					LocalDateTime.of(today.with(java.time.DayOfWeek.SUNDAY), LocalTime.MAX));
			case "last week" -> {
				LocalDate mon = today.with(java.time.DayOfWeek.MONDAY).minusWeeks(1);
				LocalDate sun = mon.plusDays(6);
				yield new Range(mon.atStartOfDay(), LocalDateTime.of(sun, LocalTime.MAX));
			}
			case "month" -> new Range(today.with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay(),
					LocalDateTime.of(today.with(TemporalAdjusters.lastDayOfMonth()), LocalTime.MAX));
			case "last month" -> {
				LocalDate first = today.with(TemporalAdjusters.firstDayOfMonth()).minusMonths(1);
				LocalDate last = first.with(TemporalAdjusters.lastDayOfMonth());
				yield new Range(first.atStartOfDay(), LocalDateTime.of(last, LocalTime.MAX));
			}
			case "year" -> new Range(today.with(TemporalAdjusters.firstDayOfYear()).atStartOfDay(),
					LocalDateTime.of(today.with(TemporalAdjusters.lastDayOfYear()), LocalTime.MAX));
			case "quarter" -> quarterRange(today);
			case "lately7" -> new Range(today.minusDays(7).atStartOfDay(), LocalDateTime.of(today, LocalTime.MAX));
			case "lately30" -> new Range(today.minusDays(30).atStartOfDay(), LocalDateTime.of(today, LocalTime.MAX));
			case "future30" -> new Range(today.atStartOfDay(), LocalDateTime.of(today.plusDays(30), LocalTime.MAX));
			default -> parseCustomRange(v, today);
		};
	}

	private static Range quarterRange(LocalDate today) {
		int q = (today.getMonthValue() - 1) / 3;
		LocalDate start = LocalDate.of(today.getYear(), q * 3 + 1, 1);
		LocalDate end = start.plusMonths(3).minusDays(1);
		return new Range(start.atStartOfDay(), LocalDateTime.of(end, LocalTime.MAX));
	}

	private static Range dayRange(LocalDate d) {
		return new Range(d.atStartOfDay(), LocalDateTime.of(d, LocalTime.MAX));
	}

	private static Range parseCustomRange(String v, LocalDate today) {
		var m = LATELY.matcher(v);
		if (m.matches()) {
			int n = Integer.parseInt(m.group(1));
			return new Range(today.minusDays(n).atStartOfDay(), LocalDateTime.of(today, LocalTime.MAX));
		}
		if (v.contains("-")) {
			String[] parts = v.split("-", 2);
			String left = parts[0].trim().replace('/', '-');
			String right = parts.length > 1 ? parts[1].trim().replace('/', '-') : "";
			if (!left.contains(":") && !right.contains(":")) {
				LocalDate start = LocalDate.parse(left, DateTimeFormatter.ISO_LOCAL_DATE);
				LocalDate end = LocalDate.parse(right, DateTimeFormatter.ISO_LOCAL_DATE);
				return new Range(start.atStartOfDay(), LocalDateTime.of(end, LocalTime.MAX));
			}
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			LocalDateTime s = LocalDateTime.parse(left.length() <= 10 ? left + " 00:00:00" : left, dtf);
			LocalDateTime e = LocalDateTime.parse(right.length() <= 10 ? right + " 23:59:59" : right, dtf);
			return new Range(s, e);
		}
		try {
			LocalDate d = LocalDate.parse(v, DateTimeFormatter.ISO_LOCAL_DATE);
			return dayRange(d);
		}
		catch (DateTimeParseException ex) {
			return dayRange(today);
		}
	}

	/**
	 * 出勤统计 time：month 或 {@code yyyy/mm/dd-yyyy/mm/dd}
	 */
	public static Range resolveAttendanceSummaryRange(String timeValue) {
		String v = StrUtil.trimToEmpty(timeValue);
		if (StrUtil.isBlank(v)) {
			v = "month";
		}
		LocalDate today = LocalDate.now(TZ);
		if ("month".equalsIgnoreCase(v)) {
			return new Range(today.with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay(),
					LocalDateTime.of(today.with(TemporalAdjusters.lastDayOfMonth()), LocalTime.MAX));
		}
		if (v.contains("-")) {
			String[] parts = v.split("-", 2);
			String left = parts[0].trim().replace('/', '-');
			String right = parts.length > 1 ? parts[1].trim().replace('/', '-') : left;
			LocalDate start = LocalDate.parse(left, DateTimeFormatter.ISO_LOCAL_DATE);
			LocalDate end = LocalDate.parse(right, DateTimeFormatter.ISO_LOCAL_DATE);
			return new Range(start.atStartOfDay(), LocalDateTime.of(end, LocalTime.MAX));
		}
		return resolveCreatedRange(v);
	}

	public static String formatAttendanceTimeLabel(Range r) {
		LocalDate s = r.startInclusive().toLocalDate();
		LocalDate e = r.endInclusive().toLocalDate();
		DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		return s.format(f) + "-" + e.format(f);
	}

}
