package com.bubblecloud.oa.api.vo.frame;

import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 组织架构人员树节点（部门 type=0，人员 type=1）。
 *
 * @author qinlei
 * @date 2026/3/29 下午4:00
 */
@Data
@Schema(description = "组织架构人员树节点")
public class FrameUserTreeNodeVO {

	@Schema(description = "节点 ID（部门为 Long；人员可能为 userId-frameId 组合字符串）")
	private Object id;

	@Schema(description = "父级 ID")
	private Object pid;

	private Long entid;

	private Integer userCount;

	private String path;

	private Object value;

	private String label;

	private String name;

	private Integer userSingleCount;

	private Integer type;

	private Boolean disabled;

	private Boolean isCheck;

	private String avatar;

	private String phone;

	private Object job;

	private String uid;

	@Schema(description = "子节点")
	private List<FrameUserTreeNodeVO> children = new ArrayList<>();

}
