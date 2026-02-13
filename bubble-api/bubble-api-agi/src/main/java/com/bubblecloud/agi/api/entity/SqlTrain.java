package com.bubblecloud.agi.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.bubblecloud.common.mybatis.base.Req;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * SQL训练示例
 *
 * @author Rampart
 * @date 2026-02-12 18:37:03
 */
@Data
@TableName("sql_train")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "SQL训练示例")
public class SqlTrain extends Req<SqlTrain> {


	/**
	* 主键
	*/
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description="主键")
    private Long id;

	/**
	* 数据源ID
	*/
    @Schema(description="数据源ID")
    private Long dsId;

	/**
	* 问题描述
	*/
    @Schema(description="问题描述")
    private String question;

	/**
	* 示例SQL
	*/
    @Schema(description="示例SQL")
    private String description;

	/**
	* 向量数据
	*/
    @Schema(description="向量数据")
    private String embedding;

	/**
	* 是否启用，0否；1是
	*/
    @Schema(description="是否启用，0否；1是")
    private Integer enabledFlag;

	/**
	* 创建时间
	*/
    @Schema(description="创建时间")
	@TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

	/**
	* 更新时间
	*/
    @Schema(description="更新时间")
	@TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}