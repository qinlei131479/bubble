package com.bubblecloud.oa.api.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bubblecloud.common.mybatis.base.Req;
import com.bubblecloud.oa.api.json.MenuPathSerializer;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统菜单，对应 eb_system_menus 表（JSON 蛇形命名以兼容 PHP 前端）。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "系统菜单")
@TableName("eb_system_menus")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SystemMenus extends Req<SystemMenus> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "父菜单ID")
	private Integer pid;

	@Schema(description = "图标")
	private String icon;

	@Schema(description = "菜单名称")
	private String menuName;

	@Schema(description = "API路径")
	private String api;

	@Schema(description = "请求方式")
	private String methods;

	@Schema(description = "唯一权限标识")
	private String uniqueAuth;

	@Schema(description = "前端路由路径")
	private String menuPath;

	@Schema(description = "菜单类型")
	private Integer menuType;

	@Schema(description = "低代码表单ID")
	private Integer crudId;

	@Schema(description = "小程序路径")
	private String uniPath;

	@Schema(description = "小程序图标")
	private String uniImg;

	@Schema(description = "位置：0默认 1置顶")
	private Integer position;

	@Schema(description = "菜单层级路径（库内为 1/2/3 串，接口 JSON 输出为数字数组）")
	@JsonSerialize(using = MenuPathSerializer.class)
	private String path;

	@Schema(description = "前端组件")
	private String component;

	@Schema(description = "层级")
	private Integer level;

	@Schema(description = "附加信息")
	private String other;

	@Schema(description = "排序")
	private Integer sort;

	@Schema(description = "企业ID")
	private Integer entid;

	@Schema(description = "类型：M菜单 B按钮 A接口 D默认")
	private String type;

	@Schema(description = "是否显示")
	private Integer isShow;

	@Schema(description = "状态")
	private Integer status;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

	@Schema(description = "删除时间")
	private LocalDateTime deletedAt;

	@TableField(exist = false)
	@Schema(description = "子菜单列表")
	private List<SystemMenus> children;

	@TableField(exist = false)
	@Schema(description = "置顶子菜单列表")
	private List<SystemMenus> topPosition;

}
