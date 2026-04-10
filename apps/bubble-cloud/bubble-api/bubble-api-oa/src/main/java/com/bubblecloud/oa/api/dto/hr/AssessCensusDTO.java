package com.bubblecloud.oa.api.dto.hr;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 绩效考核统计查询（对齐 PHP census / census_bar 条件）。
 *
 * @author qinlei
 * @date 2026/4/7 11:00
 */
@Data
@Schema(description = "绩效考核统计查询")
public class AssessCensusDTO {

	@Schema(description = "周期类型")
	private Integer period;

	@Schema(description = "开始（日期或年份字符串）")
	private String start;

	@Schema(description = "结束")
	private String end;

	@Schema(description = "视角：0自己 1下级 2人事")
	private Integer types;

	@Schema(description = "部门")
	private Long frameId;

	@Schema(description = "用户ID列表")
	private List<Long> uid;

	@Schema(description = "时间范围简写")
	private String time;

	@Schema(description = "企业ID（人事统计）")
	private Long entid;

	@Schema(description = "被考核人筛选")
	private List<Long> testUid;

	@Schema(description = "编号")
	private String number;

}
