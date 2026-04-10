package com.bubblecloud.oa.api.dto.program;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 项目保存 DTO。
 *
 * @author qinlei
 * @date 2026/4/6 15:30
 */
@Data
@Schema(description = "项目保存")
public class ProgramSaveDTO {

	@Schema(description = "名称")
	private String name;

	@Schema(description = "负责人ID")
	private Long uid;

	@Schema(description = "客户ID")
	private Long eid;

	@Schema(description = "合同ID")
	private Long cid;

	@Schema(description = "状态")
	private Integer status;

	@Schema(description = "描述")
	private String describe;

	@Schema(description = "成员ID列表")
	private List<Long> members;

	@Schema(description = "开始日期")
	private LocalDate startDate;

	@Schema(description = "结束日期")
	private LocalDate endDate;

}
