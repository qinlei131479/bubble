package com.bubblecloud.oa.api.vo.memorial;

import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 备忘录分类树节点（供 el-tree：id、name、children、count）。
 *
 * @author qinlei
 * @date 2026/4/5
 */
@Data
@Schema(description = "备忘录分类树节点")
public class MemorialCategoryTreeNodeVO {

	@Schema(description = "分类主键")
	private Long id;

	@Schema(description = "名称")
	private String name;

	@Schema(description = "上级ID")
	private Integer pid;

	@Schema(description = "该文件夹下备忘录数量")
	private long count;

	@Schema(description = "子节点")
	private List<MemorialCategoryTreeNodeVO> children = new ArrayList<>();

}
