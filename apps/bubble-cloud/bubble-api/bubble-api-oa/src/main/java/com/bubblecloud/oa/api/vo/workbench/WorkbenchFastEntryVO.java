package com.bubblecloud.oa.api.vo.workbench;

import java.util.List;

import lombok.Data;

/**
 * GET /ent/user/work/menus 响应：cates + checkd。
 *
 * @author qinlei
 * @date 2026/3/29 下午5:00
 */
@Data
public class WorkbenchFastEntryVO {

	private List<WorkbenchQuickCateVO> cates;

	private List<WorkbenchQuickItemVO> checkd;

}
