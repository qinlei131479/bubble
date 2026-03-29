package com.bubblecloud.oa.api.vo.frame;

import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 部门列表树节点（对齐 PHP Frame index / get_tree_children key=value）。
 *
 * @author qinlei
 * @date 2026/3/29 下午4:00
 */
@Data
@Schema(description = "部门列表树节点")
public class FrameDepartmentTreeNodeVO {

	@Schema(description = "父级 ID")
	private Integer pid;

	@Schema(description = "层级路径")
	private String path;

	@Schema(description = "节点值（部门 ID）")
	private Long value;

	@Schema(description = "展示名称")
	private String label;

	@Schema(description = "用户数量（含子部门）")
	private Integer userCount;

	@Schema(description = "本部门人数")
	private Integer userSingleCount;

	@Schema(description = "是否勾选")
	private Boolean isCheck;

	@Schema(description = "子节点")
	private List<FrameDepartmentTreeNodeVO> children = new ArrayList<>();

}
