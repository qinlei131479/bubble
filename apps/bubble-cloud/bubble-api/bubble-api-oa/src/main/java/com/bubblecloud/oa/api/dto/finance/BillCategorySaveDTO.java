package com.bubblecloud.oa.api.dto.finance;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 财务分类保存 DTO。
 *
 * @author qinlei
 * @date 2026/4/6 15:30
 */
@Data
@Schema(description = "财务分类保存")
public class BillCategorySaveDTO {

	@Schema(description = "祖先路径 ID 列表（PHP path 数组）")
	private List<Long> path;

	@Schema(description = "名称")
	private String name;

	@Schema(description = "上级ID（可由 path 推导）")
	private Integer pid;

	@Schema(description = "0支出 1收入")
	private Integer types;

	@Schema(description = "企业ID")
	private Long entid;

	@Schema(description = "排序")
	private Integer sort;

}
