package com.bubblecloud.oa.api.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GET /ent/user/scan_key 返回。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScanKeyVO {

	private String key;

	private String expireTime;

}
