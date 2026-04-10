package com.bubblecloud.oa.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建成功返回主键（对齐 PHP success 第二参数 {@code id}）。
 *
 * @author qinlei
 * @date 2026/4/5 18:30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "新建资源主键")
public class CreatedIdVO {

	@Schema(description = "主键 id")
	private Long id;

}
