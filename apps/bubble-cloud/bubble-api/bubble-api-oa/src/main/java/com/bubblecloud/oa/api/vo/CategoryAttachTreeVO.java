package com.bubblecloud.oa.api.vo;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 附件分类树节点（与 PHP {@code get_tree_children} 前端字段对齐）。
 *
 * @author qinlei
 * @date 2026/4/4 14:00
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "附件分类树节点")
public class CategoryAttachTreeVO {

	@Schema(description = "主键")
	private Long id;

	@Schema(description = "父级")
	private Integer pid;

	@Schema(description = "分类名")
	private String cateName;

	@Schema(description = "路径")
	private String path;

	@Schema(description = "排序")
	private Integer sort;

	@Schema(description = "图标")
	private String pic;

	@Schema(description = "是否显示")
	private Integer isShow;

	@Schema(description = "层级")
	private Integer level;

	@Schema(description = "类型")
	private String type;

	@Schema(description = "关键词")
	private String keyword;

	@Schema(description = "企业 ID")
	private Integer entid;

	@Schema(description = "子节点")
	private List<CategoryAttachTreeVO> children = new ArrayList<>();

}
