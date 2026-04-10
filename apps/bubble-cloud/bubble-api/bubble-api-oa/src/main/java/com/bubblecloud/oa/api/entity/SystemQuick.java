package com.bubblecloud.oa.api.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bubblecloud.common.mybatis.base.Req;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 快捷入口，对应 eb_system_quick 表。
 *
 * @author qinlei
 * @date 2026/3/30 下午11:45
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "快捷入口")
@TableName("eb_system_quick")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SystemQuick extends Req<SystemQuick> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Integer id;

	@Schema(description = "标题")
	private String name;

	@Schema(description = "分类ID")
	private Integer cid;

	@Schema(description = "PC 地址")
	@TableField("pc_url")
	private String pcUrl;

	@Schema(description = "移动端地址")
	@TableField("uni_url")
	private String uniUrl;

	@Schema(description = "图标")
	private String image;

	@Schema(description = "排序")
	private Integer sort;

	@Schema(description = "菜单类型 0 个人 1 企业")
	private Integer types;

	@Schema(description = "PC 是否显示")
	@TableField("pc_show")
	private Integer pcShow;

	@Schema(description = "移动端是否显示")
	@TableField("uni_show")
	private Integer uniShow;

	@Schema(description = "状态")
	private Integer status;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

}
