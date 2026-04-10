package com.bubblecloud.oa.api.vo;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户菜单响应。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户菜单响应")
public class MenusVO {

	@Schema(description = "菜单树（与 PHP 原接口字段一致）")
	private List<MenuTreeNodeVO> menu;

	@Schema(description = "按钮权限标识列表")
	private List<String> roles;

}
