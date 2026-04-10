package com.bubblecloud.oa.api.vo.program;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 任务树节点（列表/详情，对齐 PHP 关联字段命名）。
 *
 * @author qinlei
 * @date 2026/4/8 11:00
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "任务树节点")
public class ProgramTaskNodeVO {

	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "名称")
	private String name;

	@Schema(description = "项目ID")
	private Long programId;

	@Schema(description = "版本ID")
	private Long versionId;

	@Schema(description = "分享编号")
	private String ident;

	@Schema(description = "层级")
	private Integer level;

	@Schema(description = "父任务ID")
	private Long pid;

	@Schema(description = "创建人")
	private Long creatorUid;

	@Schema(description = "负责人ID")
	private Long uid;

	@Schema(description = "状态")
	private Integer status;

	@Schema(description = "优先级")
	private Integer priority;

	@Schema(description = "计划开始")
	private LocalDate planStart;

	@Schema(description = "计划结束")
	private LocalDate planEnd;

	@Schema(description = "排序")
	private Integer sort;

	@Schema(description = "路径数组（解析自 path）")
	private List<Long> path = new ArrayList<>();

	@Schema(description = "描述")
	private String describe;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

	@Schema(description = "子任务")
	private List<ProgramTaskNodeVO> children = new ArrayList<>();

	@Schema(description = "负责人对象列表（PHP admins）")
	private List<ProgramAdminLiteVO> admins = new ArrayList<>();

	@Schema(description = "协作者")
	private List<ProgramAdminLiteVO> members = new ArrayList<>();

	@Schema(description = "项目")
	private ProgramMiniVO program;

	@Schema(description = "版本")
	private ProgramMiniVO version;

	@Schema(description = "创建人信息")
	private List<ProgramAdminLiteVO> creator = new ArrayList<>();

	@Schema(description = "父任务")
	private ProgramMiniVO parent;

	@Schema(description = "当前用户可操作")
	private Boolean operate;

}
