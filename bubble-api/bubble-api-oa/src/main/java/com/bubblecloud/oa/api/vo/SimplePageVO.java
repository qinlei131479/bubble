package com.bubblecloud.oa.api.vo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 简易分页（兼容 PHP 列表分页占位与 MyBatis-Page）。
 *
 * @author qinlei
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "简易分页")
public class SimplePageVO {

	@Schema(description = "当前页")
	private int current;

	@Schema(description = "每页条数")
	private int size;

	@Schema(description = "总条数")
	private long total;

	@Schema(description = "数据列表")
	private List<Object> records = Collections.emptyList();

	public static SimplePageVO empty(int current, int size) {
		return new SimplePageVO(current, size, 0L, Collections.emptyList());
	}

	public static SimplePageVO of(int current, int size, long total, List<?> records) {
		return new SimplePageVO(current, size, total, new ArrayList<>(records));
	}

}
