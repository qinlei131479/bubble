package com.bubblecloud.oa.api.vo.company;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 档案列表/详情中的部门节点（对齐 PHP {@code frames} 关联字段名）。
 *
 * @author qinlei
 * @date 2026/4/5
 */
@Data
@Schema(description = "员工档案部门项")
public class CompanyCardFrameVO {

	@Schema(description = "部门ID")
	private Long id;

	@Schema(description = "部门名称")
	private String name;

	@Schema(description = "部门人数（含子部门）")
	@JsonProperty("user_count")
	private Integer userCount;

	@Schema(description = "是否主部门 1=是")
	@JsonProperty("is_mastart")
	private Integer isMastart;

	@Schema(description = "是否部门主管 1=是")
	@JsonProperty("is_admin")
	private Integer isAdmin;

	@Schema(description = "上级主管用户ID")
	@JsonProperty("superior_uid")
	private Long superiorUid;

}
