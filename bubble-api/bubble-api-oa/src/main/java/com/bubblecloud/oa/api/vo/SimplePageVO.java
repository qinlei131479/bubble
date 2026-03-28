package com.bubblecloud.oa.api.vo;

import java.util.Collections;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 简易分页占位（未实现业务模块前返回空数据）。
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

}
