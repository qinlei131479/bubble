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
 * 数据字典项，对应 eb_dict_data 表。
 *
 * @author qinlei
 * @date 2026/3/30 下午10:10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "字典数据")
@TableName("eb_dict_data")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DictData extends Req<DictData> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "数据名称")
	private String name;

	@Schema(description = "数据值")
	private String value;

	@Schema(description = "上级数据值")
	private String pid;

	@Schema(description = "字典类型ID")
	@TableField("type_id")
	private Integer typeId;

	@Schema(description = "字典类型名称/标识")
	@TableField("type_name")
	private String typeName;

	@Schema(description = "数据层级")
	private Integer level;

	@Schema(description = "排序")
	private Integer sort;

	@Schema(description = "颜色")
	private String color;

	@Schema(description = "状态：1 开启 0 关闭")
	private Integer status;

	@Schema(description = "是否默认")
	@TableField("is_default")
	private Integer isDefault;

	@Schema(description = "备注")
	private String mark;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

}
