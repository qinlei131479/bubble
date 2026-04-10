package com.bubblecloud.oa.api.dto.frame;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 批量查询用户所属部门（员工档案列表附加）。
 *
 * @author qinlei
 * @date 2026/4/5
 */
@Data
@Schema(description = "员工档案部门批量行（Mapper 结果）")
public class FrameAssistCardBatchRow {

	@Schema(description = "用户ID")
	private Long userId;

	@Schema(description = "部门ID")
	private Long frameId;

	@Schema(description = "部门名称")
	private String frameName;

	@Schema(description = "部门人数（含子部门）")
	private Integer userCount;

	@Schema(description = "是否主部门")
	private Integer isMastart;

	@Schema(description = "是否部门主管")
	private Integer isAdmin;

	@Schema(description = "上级主管用户ID")
	private Long superiorUid;

}
