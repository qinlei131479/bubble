package com.bubblecloud.oa.api.vo;

import java.util.Collections;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 与 PHP {@code listData} 对齐：{@code list} + {@code count}。
 *
 * @author qinlei
 * @date 2026/4/2 10:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "列表与总数")
public class ListCountVO<T> {

	@Schema(description = "数据列表")
	private List<T> list = Collections.emptyList();

	@Schema(description = "总条数")
	private long count;

	public static <T> ListCountVO<T> of(List<T> list, long count) {
		return new ListCountVO<>(list == null ? Collections.emptyList() : list, count);
	}

}
