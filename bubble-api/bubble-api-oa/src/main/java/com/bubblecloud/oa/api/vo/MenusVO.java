package com.bubblecloud.oa.api.vo;

import java.util.List;

import com.bubblecloud.oa.api.entity.SystemMenus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户菜单响应。
 *
 * @author qinlei
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户菜单响应")
public class MenusVO {

	@Schema(description = "菜单树")
	private List<SystemMenus> menu;

	@Schema(description = "按钮权限标识列表")
	private List<String> roles;

}
