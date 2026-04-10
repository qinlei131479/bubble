package com.bubblecloud.oa.api.vo.memorial;

import java.util.Collections;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 备忘录分类列表（对齐 PHP {@code total} + {@code tree}）。
 *
 * @author qinlei
 * @date 2026/4/5
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "备忘录分类树与总数")
public class MemorialCategoryIndexVO {

	@Schema(description = "该用户备忘录总数")
	private long total;

	@Schema(description = "用户自建文件夹树（types=1）")
	private List<MemorialCategoryTreeNodeVO> tree = Collections.emptyList();

}
