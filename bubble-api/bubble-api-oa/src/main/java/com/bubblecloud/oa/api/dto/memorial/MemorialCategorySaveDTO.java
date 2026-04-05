package com.bubblecloud.oa.api.dto.memorial;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 保存备忘录分类请求 DTO。
 *
 * @author qinlei
 * @date 2026/4/5
 */
@Data
@Schema(description = "保存备忘录分类")
public class MemorialCategorySaveDTO {

	@Schema(description = "级联路径（与 PHP 一致，取最后一级为父级）")
	private List<Integer> path;

	@Schema(description = "分类名称")
	private String name;

	@Schema(description = "父级分类ID")
	private Integer pid;

	@Schema(description = "用户UID")
	private String uid;

}
