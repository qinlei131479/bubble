package com.bubblecloud.biz.oa.attendance;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import com.bubblecloud.oa.api.dto.attendance.AttendanceShiftRuleSegmentDTO;
import com.bubblecloud.oa.api.dto.attendance.AttendanceShiftSaveDTO;
import com.bubblecloud.oa.api.entity.AttendanceShiftRule;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 班次规则校验与构建（对齐 PHP {@code AttendanceShiftService#getRules}/{@code getRule}）。
 *
 * @author qinlei
 * @date 2026/4/7 12:15
 */
public final class AttendanceShiftRuleValidation {

	private static final ZoneId TZ = ZoneId.of("Asia/Shanghai");

	private static final DateTimeFormatter HM = DateTimeFormatter.ofPattern("H:mm");

	private AttendanceShiftRuleValidation() {
	}

	public static ZoneId zone() {
		return TZ;
	}

	public static List<AttendanceShiftRule> buildRules(AttendanceShiftSaveDTO dto) {
		int number = ObjectUtil.defaultIfNull(dto.getNumber(), 1);
		List<AttendanceShiftRule> rules = new ArrayList<>();
		AttendanceShiftRule r1 = fromSegment(dto.getNumber1(), 1);
		rules.add(r1);
		if (number == 2) {
			AttendanceShiftRule r2 = fromSegment(dto.getNumber2(), 2);
			assertTwoShiftNoOverlap(r1, r2);
			rules.add(r2);
		}
		r1.setFirstDayAfter(0);
		return rules;
	}

	private static AttendanceShiftRule fromSegment(AttendanceShiftRuleSegmentDTO data, int ruleNumber) {
		if (data == null) {
			throw new IllegalArgumentException("请设置班次规则");
		}
		AttendanceShiftRule rule = new AttendanceShiftRule();
		rule.setNumber(ruleNumber);
		int late = ObjectUtil.defaultIfNull(data.getLate(), 0);
		int extremeLate = ObjectUtil.defaultIfNull(data.getExtremeLate(), 0);
		if (extremeLate <= late) {
			throw new IllegalArgumentException("严重迟到值要大于迟到");
		}
		int lateLack = ObjectUtil.defaultIfNull(data.getLateLackCard(), 0);
		if (lateLack <= extremeLate) {
			throw new IllegalArgumentException("晚到缺卡值要大于严重迟到");
		}
		int earlyLeave = ObjectUtil.defaultIfNull(data.getEarlyLeave(), 0);
		int earlyLack = ObjectUtil.defaultIfNull(data.getEarlyLackCard(), 0);
		if (earlyLack <= earlyLeave) {
			throw new IllegalArgumentException("半天缺卡的值要大于早退");
		}
		rule.setLate(late);
		rule.setExtremeLate(extremeLate);
		rule.setLateLackCard(lateLack);
		rule.setEarlyCard(ObjectUtil.defaultIfNull(data.getEarlyCard(), 0));
		rule.setEarlyLeave(earlyLeave);
		rule.setEarlyLackCard(earlyLack);
		rule.setDelayCard(ObjectUtil.defaultIfNull(data.getDelayCard(), 0));
		rule.setFreeClock(ObjectUtil.defaultIfNull(data.getFreeClock(), 0));
		rule.setFirstDayAfter(ObjectUtil.defaultIfNull(data.getFirstDayAfter(), 0));
		rule.setSecondDayAfter(ObjectUtil.defaultIfNull(data.getSecondDayAfter(), 0));
		rule.setWorkHours(StrUtil.trim(StrUtil.nullToEmpty(data.getWorkHours())));
		rule.setOffHours(StrUtil.trim(StrUtil.nullToEmpty(data.getOffHours())));
		if (rule.getWorkHours().isEmpty() || rule.getOffHours().isEmpty()) {
			throw new IllegalArgumentException("请设置上下班时间");
		}
		LocalDate ref = LocalDate.now(TZ);
		LocalDateTime workStart = workInstant(rule, ref, true);
		LocalDateTime offEnd = workInstant(rule, ref, false);
		if (!workStart.isBefore(offEnd)) {
			throw new IllegalArgumentException("下班时间要晚于上班时间");
		}
		int earlyCard = ObjectUtil.defaultIfNull(data.getEarlyCard(), 0);
		int delayCard = ObjectUtil.defaultIfNull(data.getDelayCard(), 0);
		LocalDateTime earlyPunch = workStart.minusSeconds(earlyCard);
		LocalDateTime latePunch = offEnd.plusSeconds(delayCard);
		if (!earlyPunch.isBefore(latePunch)) {
			throw new IllegalArgumentException("提前打卡时间要早于延后打卡时间");
		}
		if (ObjectUtil.isNotNull(data.getId()) && data.getId() > 0) {
			rule.setId(data.getId());
		}
		return rule;
	}

	private static void assertTwoShiftNoOverlap(AttendanceShiftRule first, AttendanceShiftRule second) {
		LocalDate ref = LocalDate.now(TZ);
		LocalDateTime workSecond = workInstant(second, ref, true)
			.minusSeconds(ObjectUtil.defaultIfNull(second.getEarlyCard(), 0));
		LocalDateTime offFirst = workInstant(first, ref, false)
			.plusSeconds(ObjectUtil.defaultIfNull(first.getDelayCard(), 0));
		if (!workSecond.isAfter(offFirst)) {
			throw new IllegalArgumentException("连续班次最早上班卡与最晚下班卡时间不能重叠");
		}
	}

	/**
	 * @param work true 上班时间点；false 下班时间点
	 */
	public static LocalDateTime workInstant(AttendanceShiftRule rule, LocalDate base, boolean work) {
		String hm = work ? rule.getWorkHours() : rule.getOffHours();
		LocalTime t = parseHm(hm);
		LocalDate d = base;
		int dayAfter = work ? ObjectUtil.defaultIfNull(rule.getFirstDayAfter(), 0)
				: ObjectUtil.defaultIfNull(rule.getSecondDayAfter(), 0);
		if (dayAfter == 1) {
			d = d.plusDays(1);
		}
		return LocalDateTime.of(d, t);
	}

	private static LocalTime parseHm(String hm) {
		try {
			return LocalTime.parse(hm.trim(), HM);
		}
		catch (DateTimeParseException e) {
			try {
				return LocalTime.parse("0" + hm.trim(), HM);
			}
			catch (DateTimeParseException e2) {
				throw new IllegalArgumentException("时间格式错误: " + hm);
			}
		}
	}

}
