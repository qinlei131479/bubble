package com.bubblecloud.oa.api.vo.program;

import java.time.LocalDate;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 项目列表行（扁平 + 统计字段）。
 *
 * @author qinlei
 * @date 2026/4/6 15:30
 */
@Data
@Schema(description = "项目列表行")
public class ProgramListItemVO {

	@Schema(description = "ID")
	private Long id;

	@Schema(description = "名称")
	private String name;

	@Schema(description = "编号")
	private String ident;

	@Schema(description = "负责人")
	private Long uid;

	@Schema(description = "客户")
	private Long eid;

	@Schema(description = "合同")
	private Long cid;

	@Schema(description = "创建人")
	private Long creatorUid;

	@Schema(description = "开始日期")
	private LocalDate startDate;

	@Schema(description = "结束日期")
	private LocalDate endDate;

	@Schema(description = "状态")
	private Integer status;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "当前用户可管理")
	private Boolean operate;

	@Schema(description = "任务统计")
	private ProgramTaskStatisticsVO taskStatistics;

}
