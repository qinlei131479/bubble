package com.bubblecloud.oa.api.vo.memorial;

import java.util.Collections;
import java.util.List;

import com.bubblecloud.oa.api.entity.UserMemorial;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 备忘录按月份分组的一行（对齐 PHP getGroupList 结构）。
 *
 * @author qinlei
 * @date 2026/4/2 10:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "备忘录月份分组")
public class UserMemorialGroupRowVO {

	@Schema(description = "展示用月份文案（如「本月」「2026年03月」）")
	private String month;

	@Schema(description = "该月下的备忘录列表")
	private List<UserMemorial> data = Collections.emptyList();

}
