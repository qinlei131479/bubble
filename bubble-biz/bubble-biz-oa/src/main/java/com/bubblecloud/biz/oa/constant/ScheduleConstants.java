package com.bubblecloud.biz.oa.constant;

/**
 * 与 PHP {@code ScheduleEnum} 对齐的日程常量。
 *
 * @author qinlei
 * @date 2026/4/6 10:00
 */
public interface ScheduleConstants {

	/** 按天重复. */
	 int REPEAT_DAY = 1;

	/** 按周重复. */
	 int REPEAT_WEEK = 2;

	/** 按月重复. */
	 int REPEAT_MONTH = 3;

	/** 按年重复. */
	 int REPEAT_YEAR = 4;

	/** 不重复. */
	 int REPEAT_NOT = 0;

	/** 操作：仅当前. */
	 int CHANGE_NOW = 0;

	/** 操作：当前及以后. */
	 int CHANGE_AFTER = 1;

	/** 操作：全部. */
	 int CHANGE_ALL = 2;

}
