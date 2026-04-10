package com.bubblecloud.oa.api.vo.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GET /ent/common/version。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonVersionVO {

	private String version;

	private Integer label;

	private String product;

}
