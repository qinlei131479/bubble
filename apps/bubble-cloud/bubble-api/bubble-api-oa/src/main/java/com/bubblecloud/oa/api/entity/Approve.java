package com.bubblecloud.oa.api.entity;

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
 * 审批配置主表，对应 eb_approve 表。
 *
 * @author qinlei
 * @date 2026/4/2 14:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "审批配置")
@TableName("eb_approve")
public class Approve extends Req<Approve> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "创建人 admin.id")
	@TableField("user_id")
	private Long userId;

	@Schema(description = "名片ID")
	@TableField("card_id")
	private Long cardId;

	@Schema(description = "企业ID")
	private Long entid;

	@Schema(description = "审批名称")
	private String name;

	@Schema(description = "图标")
	private String icon;

	@Schema(description = "颜色")
	private String color;

	@Schema(description = "说明")
	private String info;

	@Schema(description = "类型")
	private Integer types;

	@Schema(description = "审批方式")
	private Integer examine;

	@Schema(description = "配置 JSON")
	private String config;

	@Schema(description = "状态")
	private Integer status;

	@Schema(description = "排序")
	private Integer sort;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

	@TableLogic
	@Schema(description = "删除时间")
	private LocalDateTime deletedAt;

	@TableField(exist = false)
	@Schema(description = "列表查询：名称模糊")
	private String nameLike;

	@TableField(exist = false)
	@Schema(description = "列表筛选：类型")
	private Integer filterTypes;

	@TableField(exist = false)
	@Schema(description = "列表筛选：状态")
	private Integer filterStatus;

	@TableField(exist = false)
	@Schema(description = "列表筛选：审批方式")
	private Integer filterExamine;

	@TableField(exist = false)
	@Schema(description = "列表筛选：企业ID")
	private Long filterEntid;

}
