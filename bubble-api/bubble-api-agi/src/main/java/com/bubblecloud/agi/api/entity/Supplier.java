package com.bubblecloud.agi.api.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.*;
import com.bubblecloud.common.mybatis.base.Req;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 实体类：AI供应商表
 *
 * @author Rampart Qin
 * @date 2026/02/11 18:33
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "AI供应商表")
@TableName("supplier")
public class Supplier extends Req<Supplier> {

	private static final long serialVersionUID = 1L;
	/**
	 * 主键
	 */
	@TableId(type = IdType.INPUT)
	@Schema(description = "主键")
	private Long id;
	/**
	 * 供应商名称
	 */
	@Schema(description = "供应商名称")
	private String name;
	/**
	 * 供应商名称描述
	 */
	@Schema(description = "供应商名称描述")
	private String description;
	/**
	 * 供应商Logo
	 */
	@Schema(description = "供应商Logo")
	private String logo;
	/**
	 * API key
	 */
	@Schema(description = "API key")
	private String apiKey;
	/**
	 * API Domain
	 */
	@Schema(description = "API Domain")
	private String apiDomain;
	/**
	 * API key 申请Domain
	 */
	@Schema(description = "API key 申请Domain")
	private String apiApplyDomain;
	/**
	 * 状态，0：正常，1：停用
	 */
	@Schema(description = "状态，0：正常，1：停用")
	private Integer status;
	/**
	 * 删除标识，0：存在，1：删除
	 */
	@TableLogic
	@Schema(description = "删除标识，0：存在，1：删除")
	private Integer delFlag;
	/**
	 * 创建人
	 */
	@Schema(description = "创建人")
	@TableField(fill = FieldFill.INSERT)
	private String createBy;
	/**
	 * 创建时间
	 */
	@Schema(description = "创建时间")
	@TableField(fill = FieldFill.INSERT)
	private LocalDateTime createTime;
	/**
	 * 更新人
	 */
	@Schema(description = "更新人")
	@TableField(fill = FieldFill.INSERT_UPDATE)
	private String updateBy;
	/**
	 * 更新时间
	 */
	@Schema(description = "更新时间")
	@TableField(fill = FieldFill.INSERT_UPDATE)
	private LocalDateTime updateTime;
}
