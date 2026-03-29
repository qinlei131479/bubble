package com.bubblecloud.oa.api.vo.placeholder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用 value/label 下拉项。
 *
 * @author qinlei
 * @date 2026/3/29 下午5:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LabelValueOptionVO {

	private Object value;

	private String label;

}
