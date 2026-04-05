package com.bubblecloud.oa.api.dto.memorial;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author qinlei
 */
@Data
@Schema(description = "保存备忘录分类")
public class MemorialCategorySaveDTO {

	/**
	 * 级联路径（与 PHP 一致，取最后一级为父级）。
	 */
	private List<Integer> path;

	private String name;

	private Integer pid;

	private String uid;

}
