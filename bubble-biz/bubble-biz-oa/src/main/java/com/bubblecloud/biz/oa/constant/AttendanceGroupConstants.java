package com.bubblecloud.biz.oa.constant;

/**
 * 考勤组成员类型（对齐 PHP AttendanceGroupEnum）。
 *
 * @author qinlei
 * @date 2026/4/2 16:00
 */
public interface AttendanceGroupConstants {

	int MEMBER = 0;

	int FILTER = 1;

	int ADMIN = 2;

	int FRAME = 3;

	int WHITELIST_MEMBER = 0;

	int WHITELIST_ADMIN = 1;

	/** 补卡类型：1 缺卡；2 迟到；3 严重迟到；4 早退；5 地点异常 */
	int[] CARD_REPLACEMENT_TYPES = { 1, 2, 3, 4, 5 };

}
