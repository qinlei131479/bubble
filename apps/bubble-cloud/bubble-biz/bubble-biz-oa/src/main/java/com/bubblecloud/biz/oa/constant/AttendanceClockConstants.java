package com.bubblecloud.biz.oa.constant;

import java.util.List;
import java.util.Set;

/**
 * 对齐 PHP {@code AttendanceClockEnum} 的常量。
 *
 * @author qinlei
 * @date 2026/4/7
 */
public interface AttendanceClockConstants {

	int NO_NEED_CLOCK = 0;

	int NORMAL = 1;

	int LATE = 2;

	int EXTREME_LATE = 3;

	int LEAVE_EARLY = 4;

	int LATE_LACK_CARD = 5;

	int EARLY_LACK_CARD = 6;

	int LACK_CARD = 7;

	int OFFICE_STAFF = 0;

	int OFFICE_OUTSIDE = 1;

	int OFFICE_ABNORMAL = 2;

	String[] SHIFT_PREFIXES = { "one", "two", "three", "four" };

	List<Integer> ALL_LACK_CARD = List.of(LATE_LACK_CARD, EARLY_LACK_CARD, LACK_CARD);

	List<Integer> ALL_LATE = List.of(LATE, EXTREME_LATE);

	List<Integer> LATE_AND_LEAVE_EARLY = List.of(LATE, EXTREME_LATE, LEAVE_EARLY);

	Set<Integer> SAME_CLOCK = Set.of(NORMAL, LATE, EXTREME_LATE, LEAVE_EARLY);

}
