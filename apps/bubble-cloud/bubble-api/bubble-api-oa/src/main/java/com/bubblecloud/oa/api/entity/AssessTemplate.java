package com.bubblecloud.oa.api.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bubblecloud.common.mybatis.base.Req;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 绩效考核模板，对应 eb_assess_template 表。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "绩效考核模板")
@TableName("eb_assess_template")
public class AssessTemplate extends Req<AssessTemplate> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "企业ID")
	private Long entid;

	@Schema(description = "模板名称")
	private String name;

	@Schema(description = "模板类型：kpi/okr")
	private String types;

	@Schema(description = "封面图")
	private String cover;

	@Schema(description = "指标IDs（JSON数组）")
	private String targetIds;

	@Schema(description = "是否收藏 0否 1是")
	private Integer isFavorite;

	@Schema(description = "状态 1启用 0停用")
	private Integer status;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

	@Schema(description = "删除时间")
	@TableLogic
	private LocalDateTime deletedAt;

}
