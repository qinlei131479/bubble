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
 * 公共分类，对应 eb_category 表（含快捷入口、附件分类等 type 区分）。
 *
 * @author qinlei
 * @date 2026/3/30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "公共分类")
@TableName("eb_category")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Category extends Req<Category> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "父级ID")
	private Integer pid;

	@Schema(description = "分类名称")
	@TableField("cate_name")
	private String cateName;

	@Schema(description = "路径")
	private String path;

	@Schema(description = "排序")
	private Integer sort;

	@Schema(description = "图标")
	private String pic;

	@Schema(description = "是否显示")
	@TableField("is_show")
	private Integer isShow;

	@Schema(description = "层级")
	private Integer level;

	@Schema(description = "分类类型")
	private String type;

	@Schema(description = "标记词")
	private String keyword;

	@Schema(description = "企业ID，0 为平台")
	private Integer entid;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

}
