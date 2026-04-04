package com.bubblecloud.oa.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * POST /ent/common/initData 请求体（与 PHP postMore version 对齐）。
 *
 * @author qinlei
 * @date 2026/4/4 16:00
 */
@Data
@Schema(description = "初始化数据查询")
public class InitDataQueryDTO {

	@Schema(description = "备份版本号")
	private String version;

}
