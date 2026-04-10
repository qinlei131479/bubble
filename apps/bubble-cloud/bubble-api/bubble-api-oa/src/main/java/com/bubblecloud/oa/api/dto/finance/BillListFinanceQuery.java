package com.bubblecloud.oa.api.dto.finance;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

/**
 * 财务流水分页/汇总查询条件（由 POST /ent/bill/list 解析而来）。
 *
 * @author qinlei
 * @date 2026/4/6 16:00
 */
@Data
public class BillListFinanceQuery {

	private Long entid;

	private Integer types;

	private Integer typeId;

	private List<Integer> cateIds;

	private String markLike;

	private LocalDateTime timeStart;

	private LocalDateTime timeEnd;

}
