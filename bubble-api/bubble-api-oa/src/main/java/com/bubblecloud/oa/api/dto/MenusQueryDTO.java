package com.bubblecloud.oa.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 用户菜单查询参数。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@Data
@Schema(description = "用户菜单查询")
public class MenusQueryDTO {

	@NotNull(message = "用户ID不能为空")
	@Schema(description = "管理员用户ID")
	private Long userId;

}
