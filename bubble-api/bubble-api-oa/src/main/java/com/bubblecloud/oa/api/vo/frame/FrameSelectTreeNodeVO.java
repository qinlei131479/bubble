package com.bubblecloud.oa.api.vo.frame;

import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 部门下拉/选择树（create/edit 表单 tree 字段）。
 *
 * @author qinlei
 * @date 2026/3/29 下午4:00
 */
@Data
@Schema(description = "部门选择树节点")
public class FrameSelectTreeNodeVO {

	private Long value;

	private String label;

	private Integer pid;

	private Boolean disabled;

	@Schema(description = "子节点")
	private List<FrameSelectTreeNodeVO> children = new ArrayList<>();

}
