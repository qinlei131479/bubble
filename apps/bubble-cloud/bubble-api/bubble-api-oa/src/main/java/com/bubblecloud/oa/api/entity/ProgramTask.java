package com.bubblecloud.oa.api.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bubblecloud.common.mybatis.base.Req;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 项目任务，对应 eb_program_task 表。
 *
 * @author qinlei
 * @date 2026/4/8 10:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "项目任务")
@TableName("eb_program_task")
public class ProgramTask extends Req<ProgramTask> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "任务名称")
	private String name;

	@Schema(description = "任务编号（分享标识）")
	private String ident;

	@Schema(description = "父任务ID")
	private Long pid;

	@Schema(description = "层级路径，如 /1/2/")
	private String path;

	@Schema(description = "顶级任务ID")
	private Long topId;

	@Schema(description = "层级 1-4")
	private Integer level;

	@Schema(description = "项目ID")
	private Long programId;

	@Schema(description = "版本ID")
	private Long versionId;

	@Schema(description = "创建人ID")
	private Long creatorUid;

	@Schema(description = "负责人ID")
	private Long uid;

	@Schema(description = "状态 0未处理 1进行中 2已解决 3已验收 4已拒绝")
	private Integer status;

	@Schema(description = "优先级")
	private Integer priority;

	@Schema(description = "计划开始")
	private LocalDate planStart;

	@Schema(description = "计划结束")
	private LocalDate planEnd;

	@Schema(description = "排序")
	private Integer sort;

	@Schema(description = "描述")
	private String describe;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

	@Schema(description = "删除时间")
	@TableLogic
	private LocalDateTime deletedAt;

	@TableField(exist = false)
	@Schema(description = "列表筛选：任务视图类型 0全部可见 1负责 2参与 3创建")
	private Integer types;

	@TableField(exist = false)
	@Schema(description = "列表筛选：名称模糊")
	private String nameLike;

	@TableField(exist = false)
	@Schema(description = "列表筛选：时间范围 JSON 或区间，由 XML 解析")
	private String time;

	@TableField(exist = false)
	@Schema(description = "列表筛选：时间字段 plan_start/plan_end/created_at")
	private String timeField;

	@TableField(exist = false)
	@Schema(description = "列表筛选：协作者用户ID（多选）")
	private java.util.List<Long> members;

	@TableField(exist = false)
	@Schema(description = "列表筛选：负责人多选")
	private java.util.List<Long> admins;

	@TableField(exist = false)
	@Schema(description = "当前操作人（数据范围）")
	private Long adminUid;

	@TableField(exist = false)
	@Schema(description = "列表筛选：项目ID IN（下拉等场景）")
	private java.util.List<Long> scopeProgramIds;

}
