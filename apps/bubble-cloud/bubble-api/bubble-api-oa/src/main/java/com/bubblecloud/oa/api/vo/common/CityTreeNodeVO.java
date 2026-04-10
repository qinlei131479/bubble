package com.bubblecloud.oa.api.vo.common;

import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 省市区树节点，字段名与 PHP {@code get_tree_children} 一致（value/label/pid/children）。
 *
 * @author qinlei
 * @date 2026/4/4 16:00
 */
@Data
@Schema(description = "城市树节点")
public class CityTreeNodeVO {

	@Schema(description = "城市编码，对应 city_id")
	private Integer value;

	@Schema(description = "名称")
	private String label;

	@Schema(description = "父级 city_id")
	private Integer pid;

	@Schema(description = "子节点")
	private List<CityTreeNodeVO> children = new ArrayList<>();

}
