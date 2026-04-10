package com.bubblecloud.oa.api.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 新建部门请求体（对齐 PHP FrameController::store）。
 *
 * @author qinlei
 * @date 2026/3/29 下午12:00
 */
@Data
@Schema(description = "新建部门参数")
public class FrameSaveDTO {

	@Schema(description = "上级路径 ID 链（末级为父部门）")
	private List<Long> path;

	@Schema(description = "部门名称")
	private String name;

	@Schema(description = "介绍")
	private String introduce;

	@Schema(description = "排序")
	private Integer sort;

	@Schema(description = "角色ID")
	private Long roleId;

	@Schema(description = "企业ID")
	private Long entid;

}
