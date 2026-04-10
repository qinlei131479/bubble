package com.bubblecloud.biz.oa.schedule;

import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.bubblecloud.biz.oa.constant.ScheduleConstants;
import com.bubblecloud.oa.api.entity.Schedule;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import lombok.RequiredArgsConstructor;

/**
 * 日程在日历视图中的周期展开，对齐 PHP {@code ScheduleService::haveSchedule} / {@code checkPeriod}。
 *
 * @author qinlei
 * @date 2026/4/6 16:00
 */
@Component
@RequiredArgsConstructor
public class ScheduleOccurrenceHelper {

	private static final DateTimeFormatter DEDUPE_DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	public record Occurrence(boolean have, int finish, LocalDateTime start, LocalDateTime end, String dedupeKey) {
	}

	@FunctionalInterface
	public interface FinishResolver {

		int resolve(long viewerId, long ownerUid, long scheduleId, LocalDateTime occStart, LocalDateTime occEnd);

	}

	private final ObjectMapper objectMapper;

	private final ZoneId zone = ZoneId.systemDefault();

	public List<LocalDate> calendarDaysForPeriodView(int periodView, LocalDate rangeStart, LocalDate rangeEnd) {
		if (ObjectUtil.isNull(rangeStart)) {
			return List.of();
		}
		return switch (ObjectUtil.defaultIfNull(periodView, 1)) {
			case 1 -> List.of(rangeStart);
			case 2 -> {
				LocalDate mon = rangeStart.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
				LocalDate sun = mon.plusDays(6);
				List<LocalDate> d = new ArrayList<>();
				for (LocalDate x = mon; !x.isAfter(sun); x = x.plusDays(1)) {
					d.add(x);
				}
				yield d;
			}
			case 3 -> {
				if (ObjectUtil.isNull(rangeEnd)) {
					yield List.of(rangeStart);
				}
				List<LocalDate> d = new ArrayList<>();
				for (LocalDate x = rangeStart; !x.isAfter(rangeEnd); x = x.plusDays(1)) {
					d.add(x);
				}
				yield d;
			}
			default -> List.of(rangeStart);
		};
	}

	public Occurrence haveSchedule(Schedule schedule, LocalDate day, long viewerId, FinishResolver finishResolver) {
		int period = ObjectUtil.defaultIfNull(schedule.getPeriod(), 0);
		int rate = ObjectUtil.defaultIfNull(schedule.getRate(), 1);
		List<Integer> dayParts = parseDaysJson(schedule.getDays());
		LocalDateTime baseStart = schedule.getStartTime();
		LocalDateTime baseEnd = schedule.getEndTime();
		if (ObjectUtil.isNull(baseStart) || ObjectUtil.isNull(baseEnd)) {
			return new Occurrence(false, -1, null, null, "");
		}
		long failEpoch = failEpochCutoff(schedule.getFailTime(), baseStart);
		LocalDateTime dayStart = day.atStartOfDay();
		LocalDateTime dayEnd = day.atTime(LocalTime.MAX);
		long dayStartE = dayStart.atZone(zone).toEpochSecond();
		long dayEndE = dayEnd.atZone(zone).toEpochSecond();

		LocalDateTime occStart = baseStart;
		LocalDateTime occEnd = baseEnd;
		boolean have = false;
		int status = -1;

		switch (period) {
			case ScheduleConstants.REPEAT_DAY -> {
				long startDay0 = baseStart.toLocalDate().atStartOfDay(zone).toEpochSecond();
				BigInteger diff = BigInteger.valueOf(dayEndE - startDay0);
				BigInteger div = BigInteger.valueOf((long) rate * 86400L);
				if (div.signum() == 0) {
					break;
				}
				long p = diff.divide(div).longValue();
				if (p >= 0) {
					occStart = baseStart.plusDays(p * rate);
					occEnd = baseEnd.plusDays(p * rate);
					if (hasOverlap(dayStartE, dayEndE, occStart, occEnd) && failEpoch >= dayEndE) {
						status = finishResolver.resolve(viewerId, schedule.getUid(), schedule.getId(), occStart,
								occEnd);
						have = true;
					}
				}
			}
			case ScheduleConstants.REPEAT_WEEK -> {
				if (dayParts.isEmpty()) {
					break;
				}
				long startDay0 = baseStart.toLocalDate().atStartOfDay(zone).toEpochSecond();
				BigInteger diff = BigInteger.valueOf(dayEndE - startDay0);
				BigInteger div = BigInteger.valueOf((long) rate * 604800L);
				if (div.signum() == 0) {
					break;
				}
				long p = diff.divide(div).longValue();
				if (p < 0) {
					break;
				}
				int dowDay = day.getDayOfWeek().getValue();
				int dowStart = baseStart.getDayOfWeek().getValue();
				for (int v : dayParts) {
					if (dowDay != v) {
						continue;
					}
					LocalDateTime s;
					LocalDateTime e;
					if (dowStart > v) {
						long pp = p + 1;
						s = baseStart.plusWeeks(pp * rate).minusDays(dowStart - v);
						e = baseEnd.plusWeeks(pp * rate).minusDays(dowStart - v);
					}
					else {
						s = baseStart.plusWeeks(p * rate).plusDays(v - dowStart);
						e = baseEnd.plusWeeks(p * rate).plusDays(v - dowStart);
					}
					if (hasOverlap(dayStartE, dayEndE, s, e) && failEpoch >= dayEndE) {
						occStart = s;
						occEnd = e;
						status = finishResolver.resolve(viewerId, schedule.getUid(), schedule.getId(), occStart,
								occEnd);
						have = true;
					}
					break;
				}
			}
			case ScheduleConstants.REPEAT_MONTH -> {
				if (dayParts.isEmpty()) {
					break;
				}
				long months = ChronoUnit.MONTHS.between(YearMonth.from(baseStart.toLocalDate()), YearMonth.from(day));
				long p = rate <= 0 ? -1 : months / rate;
				if (p < 0) {
					break;
				}
				int domDay = day.getDayOfMonth();
				int domStart = baseStart.getDayOfMonth();
				for (int v : dayParts) {
					if (domDay != v) {
						continue;
					}
					LocalDateTime s;
					LocalDateTime e;
					if (domStart > v) {
						s = baseStart.plusMonths(p * rate).minusDays(domStart - v);
						e = baseEnd.plusMonths(p * rate).minusDays(domStart - v);
					}
					else {
						s = baseStart.plusMonths(p * rate).plusDays(v - domStart);
						e = baseEnd.plusMonths(p * rate).plusDays(v - domStart);
					}
					if (s.atZone(zone).toEpochSecond() >= baseStart.atZone(zone).toEpochSecond()
							&& hasOverlap(dayStartE, dayEndE, s, e) && failEpoch >= dayEndE) {
						occStart = s;
						occEnd = e;
						status = finishResolver.resolve(viewerId, schedule.getUid(), schedule.getId(), occStart,
								occEnd);
						have = true;
					}
					break;
				}
				if (!have && hasOverlap(dayStartE, dayEndE, baseStart, baseEnd)
						&& failEpoch >= baseStart.atZone(zone).toEpochSecond()) {
					occStart = baseStart;
					occEnd = baseEnd;
					status = finishResolver.resolve(viewerId, schedule.getUid(), schedule.getId(), occStart, occEnd);
					have = true;
				}
			}
			case ScheduleConstants.REPEAT_YEAR -> {
				long years = ChronoUnit.YEARS.between(baseStart.toLocalDate(), day);
				long p = rate <= 0 ? -1 : years / rate;
				if (p >= 0) {
					LocalDateTime s = baseStart.plusYears(p * rate);
					LocalDateTime e = baseEnd.plusYears(p * rate);
					if (s.atZone(zone).toEpochSecond() >= baseStart.atZone(zone).toEpochSecond()
							&& hasOverlap(dayStartE, dayEndE, s, e) && failEpoch >= dayEndE) {
						occStart = s;
						occEnd = e;
						status = finishResolver.resolve(viewerId, schedule.getUid(), schedule.getId(), occStart,
								occEnd);
						have = true;
					}
				}
			}
			default -> {
				if (hasOverlap(dayStartE, dayEndE, baseStart, baseEnd)
						&& failEpoch >= baseStart.atZone(zone).toEpochSecond()) {
					occStart = baseStart;
					occEnd = baseEnd;
					status = finishResolver.resolve(viewerId, schedule.getUid(), schedule.getId(), occStart, occEnd);
					have = true;
				}
			}
		}
		if (!have) {
			return new Occurrence(false, -1, null, null, "");
		}
		String k = DigestUtil.md5Hex(fmt(occStart) + schedule.getId() + fmt(occEnd));
		return new Occurrence(true, status, occStart, occEnd, k);
	}

	private static String fmt(LocalDateTime t) {
		return t == null ? "" : DEDUPE_DT.format(t);
	}

	private long failEpochCutoff(LocalDateTime failTime, LocalDateTime baseStart) {
		if (ObjectUtil.isNull(failTime)) {
			return baseStart.plusYears(5).atZone(zone).toEpochSecond();
		}
		return failTime.atZone(zone).toEpochSecond();
	}

	private boolean hasOverlap(long start1, long end1, LocalDateTime s2, LocalDateTime e2) {
		long s2e = s2.atZone(zone).toEpochSecond();
		long e2e = e2.atZone(zone).toEpochSecond();
		return !(end1 < s2e || start1 > e2e);
	}

	private List<Integer> parseDaysJson(String json) {
		if (StrUtil.isBlank(json)) {
			return Collections.emptyList();
		}
		try {
			List<Integer> list = objectMapper.readValue(json, new TypeReference<>() {
			});
			return list == null ? Collections.emptyList() : list;
		}
		catch (Exception e) {
			return Collections.emptyList();
		}
	}

}
