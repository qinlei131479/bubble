package com.bubblecloud.oa.api.vo;

import java.util.List;

import com.bubblecloud.oa.api.json.AdminMenuPathSerializer;
import com.bubblecloud.oa.api.json.MenuPathSerializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户菜单树节点，字段与 PHP 原接口对齐（不含扩展字段与 {@code top_position}）。
 *
 * @author qinlei
 * @date 2026/3/28 下午4:30
 */
@Data
@Schema(description = "菜单树节点")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MenuTreeNodeVO {

	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "父菜单ID")
	private Integer pid;

	@Schema(description = "菜单名称")
	private String menuName;

	@Schema(description = "前端路由路径（含 /admin 前缀）")
	@JsonSerialize(using = AdminMenuPathSerializer.class)
	private String menuPath;

	@Schema(description = "小程序路径")
	private String uniPath;

	@Schema(description = "小程序图标")
	private String uniImg;

	@Schema(description = "图标")
	private String icon;

	@Schema(description = "层级路径（数字数组）")
	@JsonSerialize(using = MenuPathSerializer.class)
	private String path;

	@Schema(description = "企业ID")
	private Integer entid;

	@Schema(description = "位置：0默认 1置顶")
	private Integer position;

	@Schema(description = "唯一权限标识")
	private String uniqueAuth;

	@Schema(description = "是否显示")
	private Integer isShow;

	@Schema(description = "前端组件")
	private String component;

	@Schema(description = "子菜单")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private List<MenuTreeNodeVO> children;

}
