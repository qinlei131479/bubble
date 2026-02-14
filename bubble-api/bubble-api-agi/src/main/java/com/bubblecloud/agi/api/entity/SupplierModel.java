package com.bubblecloud.agi.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.bubblecloud.common.mybatis.base.Req;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * AI供应商模型表
 *
 * @author Rampart
 * @date 2026-02-12 14:04:58
 */
@Data
@TableName("supplier_model")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "AI供应商模型表")
public class SupplierModel extends Req<SupplierModel> {


	/**
	 * 主键
	 */
	@TableId(type = IdType.ASSIGN_ID)
	@Schema(description = "主键")
	@NotNull(message = "模型ID不为空", groups = {Update.class})
	private Long id;

	/**
	 * ai供应商表(ai_supplier)的Id
	 */
	@Schema(description = "ai供应商(ai_supplier)的Id")
	@NotNull(message = "供应商不能为空", groups = {CreateUpdate.class})
	private Long supplierId;

	/**
	 * 模型名称（供应商名称）
	 */
	@Schema(description = "模型名称")
	@NotBlank(message = "模型名称不能为空", groups = {CreateUpdate.class})
	private String name;

	/**
	 * 模型名称（别名）
	 */
	@Schema(description = "模型名称（别名）")
	@NotBlank(message = "模型别名不能为空", groups = {CreateUpdate.class})
	private String baseModel;

	/**
	 * 模型类型: 1:聊天, 2:推理, 3:向量，4：排序，5：图片，6：视觉
	 */
	@Schema(description = "模型类型: 1:聊天, 2:推理, 3:向量，4：排序，5：图片，6：视觉")
	@NotBlank(message = "模型类型不能为空", groups = {CreateUpdate.class})
	private String modelType;

	/**
	 * 模型上下文长度
	 */
	@Schema(description = "模型上下文长度")
	private String contextLength;

	/**
	 * 模型描述
	 */
	@Schema(description = "模型描述")
	private String description;

	/**
	 * 默认模型，0：否，1：是
	 */
	@Schema(description = "默认模型，0：否，1：是")
	@NotBlank(message = "默认模型不能为空", groups = {CreateUpdate.class})
	private String defaultFlag;

	/**
	 * 模型扩展配置
	 */
	@Schema(description = "模型扩展配置")
	private String extConfig;

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

	/**
	 * 删除标识，0：存在，1：删除
	 */
	@TableLogic
	@TableField(fill = FieldFill.INSERT)
	@Schema(description = "删除标识，0：存在，1：删除")
	private String delFlag;

	/**
	 * api_key
	 */
	@TableField(exist = false)
	@Schema(description = "api_key")
	private String apiKey;

	/**
	 * 调用模型接口地址
	 */
	@TableField(exist = false)
	@Schema(description = "模型接口地址")
	private String apiDomain;

	/**
	 * 供应商名称
	 */
	@TableField(exist = false)
	@Schema(description = "供应商名称")
	private String supplierName;

}