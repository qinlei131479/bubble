package com.bubblecloud.oa.api.vo.menu;

import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 企业菜单管理树节点（对齐 PHP getAllMenusList）。
 *
 * @author qinlei
 * @date 2026/3/29 下午4:00
 */
@Data
@Schema(description = "企业菜单树节点")
public class SystemMenusTreeNodeVO {

	private Long id;

	private Long pid;

	private String menuName;

	private Integer isShow;

	private String type;

	private Integer sort;

	@Schema(description = "子节点")
	private List<SystemMenusTreeNodeVO> children = new ArrayList<>();

}
