package com.bubblecloud.oa.api.dto.company;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * 员工档案列表查询条件（由 POST body 解析）。
 *
 * @author qinlei
 */
@Data
public class CompanyCardListQuery {

	private long entid;

	private List<Integer> types = new ArrayList<>();

	private String timeStart;

	private String timeEnd;

	/** true=按入职时间 work_time；false=按离职时间 quit_time */
	private boolean workTimeRange = true;

	private Integer sex;

	private String education;

	private Integer isPart;

	private Integer status;

	private Integer singleType;

	private String search;

	private Integer frameId;

	private int offset;

	private int limit;

	/** default | quit */
	private String sortMode = "default";

}
