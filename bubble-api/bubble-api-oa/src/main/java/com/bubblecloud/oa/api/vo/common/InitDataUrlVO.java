package com.bubblecloud.oa.api.vo.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * POST /ent/common/initData 返回默认数据包地址。
 *
 * @author qinlei
 * @date 2026/4/4 16:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "初始化数据 URL")
public class InitDataUrlVO {

	@Schema(description = "完整下载 URL，无数据为空串")
	private String url;

}
