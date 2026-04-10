package com.bubblecloud.oa.api.vo.crm;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 名称与计数的简单统计行（图表/排行）。
 *
 * @author qinlei
 * @date 2026/4/3 12:00
 */
@Data
@Schema(description = "名称计数")
public class NamedCountVO {

	@Schema(description = "维度键或名称")
	private String name;

	@Schema(description = "数量")
	private Long cnt;

}
