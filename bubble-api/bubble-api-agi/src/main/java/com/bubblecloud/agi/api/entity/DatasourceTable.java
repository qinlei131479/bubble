package com.bubblecloud.agi.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.bubblecloud.common.mybatis.base.Req;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 数据源授权
 *
 * @author Rampart
 * @date 2026-02-13 16:47:36
 */
@Data
@TableName("datasource_table")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "数据源授权")
public class DatasourceTable extends Req<DatasourceTable> {


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
	* 是否选中，0否1是
	*/
    @Schema(description="是否选中，0否1是")
    private Integer checkedFlag;

	/**
	* 表名
	*/
    @Schema(description="表名")
    private String tableName;

	/**
	* 表注释
	*/
    @Schema(description="表注释")
    private String tableComment;

	/**
	* 表注释（自定义）
	*/
    @Schema(description="表注释（自定义）")
    private String customComment;

	/**
	* 表结构 embedding (JSON 数组字符串)
	*/
    @Schema(description="表结构 embedding (JSON 数组字符串)")
    private String embedding;

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