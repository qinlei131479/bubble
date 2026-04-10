package com.bubblecloud.oa.api.vo.workbench;

import java.util.List;

import lombok.Data;

/**
 * GET /statistics_type：list + select。
 *
 * @author qinlei
 * @date 2026/3/29 下午5:00
 */
@Data
public class WorkbenchStatisticsTypeVO {

	private List<WorkbenchStatisticOptionVO> list;

	private List<WorkbenchStatisticOptionVO> select;

}
