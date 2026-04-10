package com.bubblecloud.oa.api.vo.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GET /ent/common/auth 商业授权占位。
 *
 * @author qinlei
 * @date 2026/3/29 下午5:30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonAuthVO {

	private Integer status;

	private Integer day;

}
