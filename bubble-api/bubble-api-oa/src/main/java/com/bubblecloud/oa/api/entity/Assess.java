package com.bubblecloud.oa.api.entity;

import java.time.LocalDate;
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
 * 绩效考核，对应 eb_assess 表。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "绩效考核")
@TableName("eb_assess")
public class Assess extends Req<Assess> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "企业ID")
	private Long entid;

	@Schema(description = "考核名称")
	private String name;

	@Schema(description = "被考核人ID(admin)")
	private Long userId;

	@Schema(description = "考核人ID(上级)")
	private Long superiorId;

	@Schema(description = "审核人ID(上上级)")
	private Long examineId;

	@Schema(description = "考核计划ID")
	private Long planId;

	@Schema(description = "考核模板ID")
	private Long templateId;

	@Schema(description = "考核开始日期")
	private LocalDate startDate;

	@Schema(description = "考核结束日期")
	private LocalDate endDate;

	@Schema(description = "自评内容")
	private String selfContent;

	@Schema(description = "自评分数")
	private java.math.BigDecimal selfScore;

	@Schema(description = "上级评分")
	private java.math.BigDecimal superiorScore;

	@Schema(description = "审核评分")
	private java.math.BigDecimal examineScore;

	@Schema(description = "最终得分")
	private java.math.BigDecimal finalScore;

	@Schema(description = "等级")
	private String level;

	@Schema(description = "状态：0待自评 1待上级评 2待审核 3已完成 4已申诉 5申诉驳回")
	private Integer status;

	@Schema(description = "备注")
	private String mark;

	@Schema(description = "是否已申诉 0否 1是")
	private Integer isAppeal;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

	@Schema(description = "删除时间")
	@TableLogic
	private LocalDateTime deletedAt;

}
