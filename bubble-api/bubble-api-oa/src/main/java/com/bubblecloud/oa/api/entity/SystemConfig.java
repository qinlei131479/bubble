package com.bubblecloud.oa.api.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bubblecloud.common.mybatis.base.Req;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统配置，对应 eb_system_config 表。
 *
 * @author qinlei
 * @date 2026/3/30 下午8:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "系统配置")
@TableName("eb_system_config")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SystemConfig extends Req<SystemConfig> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "配置分类")
	private String category;

	@Schema(description = "配置字段键")
	@TableField("`key`")
	private String configKey;

	@Schema(description = "配置名称")
	@TableField("key_name")
	private String keyName;

	@Schema(description = "控件类型(文本框/单选等)")
	private String type;

	@Schema(description = "表单类型 input/select 等")
	@TableField("input_type")
	private String inputType;

	@Schema(description = "配置分类 id")
	@TableField("cate_id")
	private Integer cateId;

	@Schema(description = "分类路径")
	private String path;

	@Schema(description = "单选/多选参数 JSON")
	private String parameter;

	@Schema(description = "上传文件格式 1单图2多图3文件")
	@TableField("upload_type")
	private Integer uploadType;

	@Schema(description = "校验规则")
	private String required;

	@Schema(description = "多行文本宽度")
	private Integer width;

	@Schema(description = "多行文本高度")
	private Integer high;

	@Schema(description = "配置值")
	private String value;

	@Schema(description = "配置简介")
	@TableField("`desc`")
	private String configDesc;

	@Schema(description = "排序")
	private Integer sort;

	@Schema(description = "企业ID，0 为总后台")
	private Integer entid;

	@Schema(description = "分后台标识")
	@TableField("ent_key")
	private String entKey;

	@Schema(description = "是否显示")
	@TableField("is_show")
	private Integer isShow;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

}
