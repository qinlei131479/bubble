package com.bubblecloud.oa.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * POST /ent/user/scan_status 查询扫码状态。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@Data
public class ScanStatusDTO {

	@NotBlank
	private String key;

}
