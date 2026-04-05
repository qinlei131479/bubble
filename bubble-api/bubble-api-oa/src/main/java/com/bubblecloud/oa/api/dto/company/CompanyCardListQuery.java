package com.bubblecloud.oa.api.dto.company;

import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 员工档案列表查询条件（由 POST body 解析）。
 *
 * @author qinlei
 * @date 2026/4/5
 */
@Data
@Schema(description = "员工档案列表查询条件")
public class CompanyCardListQuery {

	@Schema(description = "企业ID")
	private long entid;

	@Schema(description = "筛选类型列表")
	private List<Integer> types = new ArrayList<>();

	@Schema(description = "时间范围开始")
	private String timeStart;

	@Schema(description = "时间范围结束")
	private String timeEnd;

	@Schema(description = "true=按入职时间 work_time；false=按离职时间 quit_time")
	private boolean workTimeRange = true;

	@Schema(description = "性别")
	private Integer sex;

	@Schema(description = "学历")
	private String education;

	@Schema(description = "是否兼职")
	private Integer isPart;

	@Schema(description = "状态")
	private Integer status;

	@Schema(description = "单一类型筛选")
	private Integer singleType;

	@Schema(description = "关键词搜索")
	private String search;

	@Schema(description = "部门ID")
	private Integer frameId;

	@Schema(description = "分页偏移")
	private int offset;

	@Schema(description = "每页条数")
	private int limit;

	@Schema(description = "排序模式：default | quit")
	private String sortMode = "default";

}
