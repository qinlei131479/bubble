package com.bubblecloud.biz.oa.schedule;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.bubblecloud.biz.oa.constant.ScheduleConstants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;

/**
 * 对齐 PHP {@code ScheduleService::getPreviousPeriod} / {@code getNextPeriod}。
 *
 * @author qinlei
 * @date 2026/4/6 18:00
 */
@Component
@RequiredArgsConstructor
public class SchedulePeriodMutationHelper {

	private final ObjectMapper objectMapper;

	public List<Integer> parseDayInts(String json) {
		if (StrUtil.isBlank(json)) {
			return List.of();
		}
		try {
			List<Integer> list = objectMapper.readValue(json, new TypeReference<>() {
			});
			return list == null ? List.of() : list;
		}
		catch (Exception e) {
			return List.of();
		}
	}

	/**
	 * @param seriesEnd 对应 PHP 第一参 {@code $info['end_time']}
	 * @param boundaryEnd 对应 PHP 第二参 {@code $data['end']}
	 */
	public LocalDateTime getPreviousPeriod(LocalDateTime seriesEnd, LocalDateTime boundaryEnd, int period, int rate,
			String daysJson) {
		List<Integer> days = new ArrayList<>(parseDayInts(daysJson));
		Zoned seriesZ = Zoned.of(seriesEnd);
		Zoned newZ = Zoned.of(boundaryEnd);
		LocalDateTime failTime = boundaryEnd;

		switch (period) {
			case ScheduleConstants.REPEAT_DAY:
				return newZ.dt.minusDays(rate);
			case ScheduleConstants.REPEAT_YEAR:
				return seriesZ.dt.minusYears(rate);
			case ScheduleConstants.REPEAT_WEEK:
				days.sort(Collections.reverseOrder());
				if (days.isEmpty()) {
					return failTime;
				}
				int dow = newZ.dowIso();
				int minD = Collections.min(days);
				int maxD = Collections.max(days);
				if (dow <= minD) {
					Zoned base = seriesZ.minusWeeks(1).startOfWeek().plusDays(maxD);
					return combineDateTime(base.toLocalDate(), seriesZ.toLocalTime());
				}
				for (int key = 0; key < days.size(); key++) {
					int val = days.get(key);
					if (dow > val) {
						Zoned base = seriesZ.startOfWeek().plusDays(val);
						return combineDateTime(base.toLocalDate(), seriesZ.toLocalTime());
					}
					if (dow == val && key > 0) {
						int prev = days.get(key - 1);
						Zoned base = seriesZ.startOfWeek().plusDays(prev);
						return combineDateTime(base.toLocalDate(), seriesZ.toLocalTime());
					}
				}
				return failTime;
			case ScheduleConstants.REPEAT_MONTH:
				days.sort(Collections.reverseOrder());
				if (days.isEmpty()) {
					return failTime;
				}
				int dom = newZ.dt.getDayOfMonth();
				minD = Collections.min(days);
				maxD = Collections.max(days);
				if (dom <= minD) {
					LocalDate failDay = seriesZ.dt.minusMonths(1)
						.with(TemporalAdjusters.firstDayOfMonth())
						.plusDays(maxD)
						.toLocalDate();
					return combineDateTime(failDay, seriesZ.toLocalTime());
				}
				for (int key = 0; key < days.size(); key++) {
					int val = days.get(key);
					if (dom > val) {
						LocalDate failDay = seriesZ.dt.with(TemporalAdjusters.firstDayOfMonth())
							.plusDays(val)
							.toLocalDate();
						return combineDateTime(failDay, seriesZ.toLocalTime());
					}
					if (dom == val && key > 0) {
						int prev = days.get(key - 1);
						LocalDate failDay = seriesZ.dt.with(TemporalAdjusters.firstDayOfMonth())
							.plusDays(prev)
							.toLocalDate();
						return combineDateTime(failDay, seriesZ.toLocalTime());
					}
				}
				return failTime;
			default:
				return failTime;
		}
	}

	public LocalDateTime[] getNextPeriod(LocalDateTime occStart, LocalDateTime occEnd, int period, int rate,
			String daysJson) {
		List<Integer> days = new ArrayList<>(parseDayInts(daysJson));
		Zoned ns = Zoned.of(occStart);
		Zoned ne = Zoned.of(occEnd);
		LocalDateTime startTime = occStart;
		LocalDateTime endTime = occEnd;

		switch (period) {
			case ScheduleConstants.REPEAT_DAY:
				startTime = ns.dt.plusDays(rate);
				endTime = ne.dt.plusDays(rate);
				break;
			case ScheduleConstants.REPEAT_YEAR:
				startTime = ns.dt.plusYears(rate);
				endTime = ne.dt.plusYears(rate);
				break;
			case ScheduleConstants.REPEAT_WEEK:
				days.sort(Integer::compareTo);
				if (days.isEmpty()) {
					break;
				}
				int dow = ns.dowIso();
				int maxD = Collections.max(days);
				int minD = Collections.min(days);
				if (dow >= maxD) {
					LocalDate startDay = ns.plusWeeks(rate).startOfWeek().plusDays(minD).minusDays(1).toLocalDate();
					startTime = combineDateTime(startDay, ns.toLocalTime());
					LocalDate endDay = ne.plusWeeks(rate).startOfWeek().plusDays(minD).minusDays(1).toLocalDate();
					endTime = combineDateTime(endDay, ne.toLocalTime());
				}
				else {
					for (int key = 0; key < days.size(); key++) {
						int val = days.get(key);
						if (dow == val && key + 1 < days.size()) {
							int next = days.get(key + 1);
							LocalDate startDay = ns.startOfWeek().plusDays(next).minusDays(1).toLocalDate();
							startTime = combineDateTime(startDay, ns.toLocalTime());
							LocalDate endDay = ne.startOfWeek().plusDays(next).minusDays(1).toLocalDate();
							endTime = combineDateTime(endDay, ne.toLocalTime());
							break;
						}
						if (dow < val) {
							LocalDate startDay = ns.startOfWeek().plusDays(val).minusDays(1).toLocalDate();
							startTime = combineDateTime(startDay, ns.toLocalTime());
							LocalDate endDay = ne.startOfWeek().plusDays(val).minusDays(1).toLocalDate();
							endTime = combineDateTime(endDay, ne.toLocalTime());
							break;
						}
					}
				}
				break;
			case ScheduleConstants.REPEAT_MONTH:
				days.sort(Integer::compareTo);
				if (days.isEmpty()) {
					break;
				}
				int dom = ns.dt.getDayOfMonth();
				int maxDom = Collections.max(days);
				int minDom = Collections.min(days);
				if (dom >= maxDom) {
					LocalDate startDay = ns.dt.plusMonths(1)
						.with(TemporalAdjusters.firstDayOfMonth())
						.plusDays(minDom)
						.minusDays(1)
						.toLocalDate();
					startTime = combineDateTime(startDay, ns.toLocalTime());
					LocalDate endDay = ne.dt.plusMonths(1)
						.with(TemporalAdjusters.firstDayOfMonth())
						.plusDays(minDom)
						.minusDays(1)
						.toLocalDate();
					endTime = combineDateTime(endDay, ne.toLocalTime());
				}
				else {
					for (int key = 0; key < days.size(); key++) {
						int val = days.get(key);
						if (dom == val && key + 1 < days.size()) {
							int next = days.get(key + 1);
							LocalDate sd = ns.dt.with(TemporalAdjusters.firstDayOfMonth())
								.plusDays(next)
								.minusDays(1)
								.toLocalDate();
							startTime = combineDateTime(sd, ns.toLocalTime());
							LocalDate ed = ne.dt.with(TemporalAdjusters.firstDayOfMonth())
								.plusDays(next)
								.minusDays(1)
								.toLocalDate();
							endTime = combineDateTime(ed, ne.toLocalTime());
							break;
						}
						if (dom < val) {
							LocalDate sd = ns.dt.with(TemporalAdjusters.firstDayOfMonth())
								.plusDays(val)
								.minusDays(1)
								.toLocalDate();
							startTime = combineDateTime(sd, ns.toLocalTime());
							LocalDate ed = ne.dt.with(TemporalAdjusters.firstDayOfMonth())
								.plusDays(val)
								.minusDays(1)
								.toLocalDate();
							endTime = combineDateTime(ed, ne.toLocalTime());
							break;
						}
					}
				}
				break;
			default:
				break;
		}
		return new LocalDateTime[] { startTime, endTime };
	}

	private static LocalDateTime combineDateTime(LocalDate date, LocalTime time) {
		LocalTime t = time == null ? LocalTime.MIDNIGHT : time;
		return LocalDateTime.of(date, t);
	}

	private static final class Zoned {

		private final LocalDateTime dt;

		private Zoned(LocalDateTime dt) {
			this.dt = dt;
		}

		static Zoned of(LocalDateTime dt) {
			return new Zoned(dt);
		}

		LocalTime toLocalTime() {
			return dt.toLocalTime();
		}

		LocalDate toLocalDate() {
			return dt.toLocalDate();
		}

		int dowIso() {
			return dt.getDayOfWeek().getValue();
		}

		Zoned startOfWeek() {
			LocalDate mon = dt.toLocalDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
			return new Zoned(LocalDateTime.of(mon, LocalTime.MIDNIGHT));
		}

		Zoned plusWeeks(long w) {
			return new Zoned(dt.plusWeeks(w));
		}

		Zoned minusWeeks(long w) {
			return new Zoned(dt.minusWeeks(w));
		}

		Zoned plusDays(long d) {
			return new Zoned(dt.plusDays(d));
		}

		Zoned minusDays(long d) {
			return new Zoned(dt.minusDays(d));
		}

	}

}
