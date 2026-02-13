package com.bubblecloud.agi.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.bubblecloud.common.mybatis.base.Req;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 数据源表字段
 *
 * @author Rampart
 * @date 2026-02-13 16:48:54
 */
@Data
@TableName("datasource_table_field")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "数据源表字段")
public class DatasourceTableField extends Req<DatasourceTableField> {


	/**
	* id
	*/
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description="id")
    private Long id;

	/**
	* 数据源ID
	*/
    @Schema(description="数据源ID")
    private Long dsId;

	/**
	* 表ID
	*/
    @Schema(description="表ID")
    private Long tableId;

	/**
	* 字段名称
	*/
    @Schema(description="字段名称")
    private String fieldName;

	/**
	* 字段类型
	*/
    @Schema(description="字段类型")
    private String fieldType;

	/**
	* 字段注释
	*/
    @Schema(description="字段注释")
    private String fieldComment;

	/**
	* 字段注释(自定义)
	*/
    @Schema(description="字段注释(自定义)")
    private String customComment;

	/**
	* 字段排序，越小越靠前
	*/
    @Schema(description="字段排序，越小越靠前")
    private Integer weight;

	/**
	* 创建时间
	*/
	@TableField(fill = FieldFill.INSERT)
    @Schema(description="创建时间")
    private LocalDateTime createTime;

	/**
	* 更新时间
	*/
	@TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description="更新时间")
    private LocalDateTime updateTime;
}