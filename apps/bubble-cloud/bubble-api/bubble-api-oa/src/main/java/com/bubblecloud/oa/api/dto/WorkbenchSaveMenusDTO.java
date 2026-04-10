package com.bubblecloud.oa.api.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * POST /ent/user/work/menus 保存快捷入口。
 *
 * @author qinlei
 * @date 2026/3/29 下午5:00
 */
@Data
@Schema(description = "保存快捷入口")
public class WorkbenchSaveMenusDTO {

	@Schema(description = "选中的快捷入口 ID 列表")
	private List<Integer> data;

}
