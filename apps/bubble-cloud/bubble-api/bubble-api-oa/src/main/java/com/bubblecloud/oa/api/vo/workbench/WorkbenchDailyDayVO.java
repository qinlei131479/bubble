package com.bubblecloud.oa.api.vo.workbench;

import lombok.Data;

/**
 * 工作台某月某日汇报摘要（对齐 PHP getMonthDailyList 单条）。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@Data
public class WorkbenchDailyDayVO {

	private Integer day;

	private String finish;

	private String plan;

}
