package com.bubblecloud.oa.api.vo.workbench;

import lombok.Data;

/**
 * 工作台顶部四宫格数量。
 *
 * @author qinlei
 * @date 2026/3/29 下午5:00
 */
@Data
public class WorkbenchCountVO {

	private Integer scheduleCount;

	private Integer applyCount;

	private Integer approveCount;

	private Integer noticeCount;

}
