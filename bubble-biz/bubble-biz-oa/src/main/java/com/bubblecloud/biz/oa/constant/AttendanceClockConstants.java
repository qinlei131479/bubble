package com.bubblecloud.biz.oa.constant;

import java.util.List;
import java.util.Set;

/**
 * 对齐 PHP {@code AttendanceClockEnum} 的常量。
 *
 * @author qinlei
 * @date 2026/4/7
 */
public final class AttendanceClockConstants {

	private AttendanceClockConstants() {
	}

	public static final int NO_NEED_CLOCK = 0;

	public static final int NORMAL = 1;

	public static final int LATE = 2;

	public static final int EXTREME_LATE = 3;

	public static final int LEAVE_EARLY = 4;

	public static final int LATE_LACK_CARD = 5;

	public static final int EARLY_LACK_CARD = 6;

	public static final int LACK_CARD = 7;

	public static final int OFFICE_STAFF = 0;

	public static final int OFFICE_OUTSIDE = 1;

	public static final int OFFICE_ABNORMAL = 2;

	public static final String[] SHIFT_PREFIXES = { "one", "two", "three", "four" };

	public static final List<Integer> ALL_LACK_CARD = List.of(LATE_LACK_CARD, EARLY_LACK_CARD, LACK_CARD);

	public static final List<Integer> ALL_LATE = List.of(LATE, EXTREME_LATE);

	public static final List<Integer> LATE_AND_LEAVE_EARLY = List.of(LATE, EXTREME_LATE, LEAVE_EARLY);

	public static final Set<Integer> SAME_CLOCK = Set.of(NORMAL, LATE, EXTREME_LATE, LEAVE_EARLY);

}
