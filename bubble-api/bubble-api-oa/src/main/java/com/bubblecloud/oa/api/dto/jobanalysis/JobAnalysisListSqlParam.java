package com.bubblecloud.oa.api.dto.jobanalysis;

import java.util.List;

import lombok.Data;

/**
 * 工作分析列表 SQL 条件（Mapper XML 入参）。
 *
 * @author qinlei
 * @date 2026/4/5 15:30
 */
@Data
public class JobAnalysisListSqlParam {

	private long entid;

	private String name;

	private Integer frameId;

	private List<Integer> subtreeFrameIds;

	private List<Integer> types;

	private Integer jobId;

	private List<Long> scopeAdminIds;

	private boolean filterByFrame;

	private boolean filterByScopeIds;

	private int limit;

	private long offset;

}
