package com.bubblecloud.oa.api.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bubblecloud.common.mybatis.base.Req;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 自定义表单字段，对应 eb_form_data 表。
 *
 * @author qinlei
 * @date 2026/3/30 下午11:45
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "表单字段")
@TableName("eb_form_data")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class FormData extends Req<FormData> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "字段唯一键")
	@TableField("`key`")
	private String fieldKey;

	@Schema(description = "字段名称")
	@TableField("key_name")
	private String keyName;

	@Schema(description = "控件类型")
	private String type;

	@Schema(description = "表单类型 input/select 等")
	@TableField("input_type")
	private String inputType;

	@Schema(description = "分组ID")
	@TableField("cate_id")
	private Long cateId;

	@Schema(description = "选项参数")
	private String param;

	@Schema(description = "小数位数")
	@TableField("decimal_place")
	private Integer decimalPlace;

	@Schema(description = "上传文件格式")
	@TableField("upload_type")
	private Integer uploadType;

	@Schema(description = "是否必填")
	private Integer required;

	@Schema(description = "占位提示")
	private String placeholder;

	@Schema(description = "最大值")
	private Integer max;

	@Schema(description = "最小值")
	private Integer min;

	@Schema(description = "字典标识")
	@TableField("dict_ident")
	private String dictIdent;

	@Schema(description = "默认值")
	private String value;

	@Schema(description = "是否唯一")
	private Integer uniqued;

	@Schema(description = "简介")
	@TableField("`desc`")
	private String fieldDesc;

	@Schema(description = "排序")
	private Integer sort;

	@Schema(description = "状态")
	private Integer status;

	@TableLogic
	@TableField("deleted_at")
	@Schema(description = "删除时间")
	private LocalDateTime deletedAt;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

}
