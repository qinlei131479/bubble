package com.bubblecloud.oa.api.vo.workbench;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 业绩统计卡片一项。
 *
 * @author qinlei
 * @date 2026/3/29 下午5:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkbenchStatisticCardVO {

	private String title;

	private String value;

}
