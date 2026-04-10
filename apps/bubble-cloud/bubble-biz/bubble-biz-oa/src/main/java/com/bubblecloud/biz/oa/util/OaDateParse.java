package com.bubblecloud.biz.oa.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import cn.hutool.core.util.StrUtil;

/**
 * @author qinlei
 * @date 2026/4/5
 */
public final class OaDateParse {

	private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE;

	private OaDateParse() {
	}

	public static LocalDate requireDate(String raw, String label) {
		if (StrUtil.isBlank(raw)) {
			throw new IllegalArgumentException(label + "不能为空");
		}
		try {
			return LocalDate.parse(raw.trim(), ISO);
		}
		catch (DateTimeParseException e) {
			throw new IllegalArgumentException(label + "格式错误");
		}
	}

	public static LocalDateTime startOfDay(String raw, String label) {
		return requireDate(raw, label).atStartOfDay();
	}

	public static void assertNotAfter(LocalDate start, LocalDate end, String message) {
		if (start.isAfter(end)) {
			throw new IllegalArgumentException(message);
		}
	}

}
