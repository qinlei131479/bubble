package com.bubblecloud.biz.oa.service.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.CalendarConfigMapper;
import com.bubblecloud.biz.oa.service.CalendarConfigService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.dto.CalendarDayItemDTO;
import com.bubblecloud.oa.api.dto.CalendarUpdateDTO;
import com.bubblecloud.oa.api.entity.CalendarConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 考勤日历配置实现。
 *
 * @author qinlei
 * @date 2026/4/2 15:00
 */
@Service
public class CalendarConfigServiceImpl extends UpServiceImpl<CalendarConfigMapper, CalendarConfig>
		implements CalendarConfigService {

	private static final ZoneId TZ = ZoneId.of("Asia/Shanghai");

	private static final DateTimeFormatter DAY_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

	@Override
	public List<String> getRestList(String time) {
		String[] yml = parseTime(time);
		int year = Integer.parseInt(yml[0]);
		int monthStart = Integer.parseInt(yml[1]);
		int monthEnd = Integer.parseInt(yml[2]);
		LocalDate start = LocalDate.of(year, monthStart, 1);
		LocalDate end = LocalDate.of(year, monthEnd, 1).with(TemporalAdjusters.lastDayOfMonth());
		Map<String, CalendarConfig> calendar = getCalendarMapByYear(year);
		return getRestDay(start, end, calendar);
	}

	private static String[] parseTime(String time) {
		if (StrUtil.isBlank(time)) {
			throw new IllegalArgumentException("时间参数不能为空");
		}
		if (time.contains("-")) {
			String[] p = time.split("-");
			if (p.length >= 2) {
				return new String[] { p[0], p[1], p[1] };
			}
		}
		return new String[] { time, "01", "12" };
	}

	private Map<String, CalendarConfig> getCalendarMapByYear(int year) {
		List<CalendarConfig> list = list(Wrappers.lambdaQuery(CalendarConfig.class).apply("YEAR(`day`) = {0}", year));
		return list.stream().collect(Collectors.toMap(CalendarConfig::getDay, c -> c, (a, b) -> a));
	}

	private List<String> getRestDay(LocalDate start, LocalDate end, Map<String, CalendarConfig> calendar) {
		List<String> restDay = new ArrayList<>();
		LocalDate d = start;
		while (!d.isAfter(end)) {
			String dayStr = d.toString();
			boolean merge = true;
			DayOfWeek dow = d.getDayOfWeek();
			boolean weekend = dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY;
			if (weekend) {
				if (calendar.containsKey(dayStr) && ObjectUtil.isNotNull(calendar.get(dayStr).getIsRest())
						&& calendar.get(dayStr).getIsRest() == 0) {
					merge = false;
				}
			}
			else {
				if (!calendar.containsKey(dayStr)) {
					merge = false;
				}
			}
			if (merge) {
				restDay.add(dayStr);
			}
			d = d.plusDays(1);
		}
		return restDay;
	}

	private Map<String, CalendarConfig> getCalendarMapYearMonth(int year, int month) {
		String prefix = String.format("%04d-%02d", year, month);
		List<CalendarConfig> list = list(
				Wrappers.lambdaQuery(CalendarConfig.class).likeRight(CalendarConfig::getDay, prefix));
		Map<String, CalendarConfig> map = new HashMap<>();
		for (CalendarConfig c : list) {
			map.put(c.getDay(), c);
		}
		return map;
	}

	private static boolean carbonWeekend(LocalDate d) {
		DayOfWeek dow = d.getDayOfWeek();
		return dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY;
	}

	@Override
	public R update(CalendarConfig req) {
		return super.update(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateCalendar(String date, CalendarUpdateDTO req) {
		if (StrUtil.isBlank(date)) {
			throw new IllegalArgumentException("日期不能为空");
		}
		List<CalendarDayItemDTO> data = ObjectUtil.isNull(req) ? null : req.getData();
		if (ObjectUtil.isEmpty(data)) {
			throw new IllegalArgumentException("参数不能为空");
		}
		LocalDate dateObj = LocalDate.parse(date, DAY_FMT);
		YearMonth nowYm = YearMonth.now(TZ);
		YearMonth targetYm = YearMonth.from(dateObj);
		if (targetYm.isBefore(nowYm)) {
			throw new IllegalArgumentException("今日及以前的日期禁止调整");
		}
		for (CalendarDayItemDTO item : data) {
			if (ObjectUtil.isNull(item) || StrUtil.isBlank(item.getDay())) {
				throw new IllegalArgumentException("时间格式错误");
			}
			LocalDate itemDay = LocalDate.parse(item.getDay(), DAY_FMT);
			if (!item.getDay().equals(itemDay.toString())) {
				throw new IllegalArgumentException("时间格式错误");
			}
		}
		Map<String, CalendarConfig> calendar = new HashMap<>(
				getCalendarMapYearMonth(dateObj.getYear(), dateObj.getMonthValue()));
		LocalDateTime now = LocalDateTime.now();
		for (CalendarDayItemDTO item : data) {
			String dayStr = item.getDay();
			LocalDate itemDay = LocalDate.parse(dayStr, DAY_FMT);
			CalendarConfig existing = calendar.get(dayStr);
			if (ObjectUtil.isNotNull(existing) && ObjectUtil.equals(existing.getIsRest(), item.getIsRest())) {
				calendar.remove(dayStr);
				continue;
			}
			boolean weekend = carbonWeekend(itemDay);
			int isRest = ObjectUtil.defaultIfNull(item.getIsRest(), 0);
			if ((isRest == 1 && weekend) || (isRest == 0 && !weekend)) {
				baseMapper.delete(Wrappers.lambdaQuery(CalendarConfig.class).eq(CalendarConfig::getDay, dayStr));
				calendar.remove(dayStr);
				continue;
			}
			baseMapper.delete(Wrappers.lambdaQuery(CalendarConfig.class).eq(CalendarConfig::getDay, dayStr));
			CalendarConfig row = new CalendarConfig();
			row.setDay(dayStr);
			row.setIsRest(isRest);
			row.setCreatedAt(now);
			row.setUpdatedAt(now);
			baseMapper.insert(row);
			calendar.remove(dayStr);
		}
		for (CalendarConfig c : calendar.values()) {
			baseMapper.deleteById(c.getId());
		}
	}

}
