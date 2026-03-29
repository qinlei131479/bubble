package com.bubblecloud.oa.api.vo.frame;

import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 权限/数据范围用部门树节点（可含虚拟「仅本人」等项）。
 *
 * @author qinlei
 * @date 2026/3/29 下午4:00
 */
@Data
@Schema(description = "权限范围部门树节点")
public class FrameAuthTreeNodeVO {

	@Schema(description = "节点 ID（部门为数字，虚拟项为字符串）")
	private Object id;

	@Schema(description = "父级 ID")
	private Integer pid;

	private Long entid;

	private Integer userCount;

	private String path;

	private Object value;

	private String label;

	private String name;

	private Integer userSingleCount;

	private Boolean disabled;

	@Schema(description = "子节点")
	private List<FrameAuthTreeNodeVO> children = new ArrayList<>();

}
