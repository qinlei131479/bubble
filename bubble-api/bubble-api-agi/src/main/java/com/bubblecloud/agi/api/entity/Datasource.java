package com.bubblecloud.agi.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.bubblecloud.common.mybatis.base.Req;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 数据源
 *
 * @author Rampart
 * @date 2026-02-13 16:45:56
 */
@Data
@TableName("datasource")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "数据源")
public class Datasource extends Req<Datasource> {


	/**
	* 主键
	*/
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description="主键")
    private Long id;

	/**
	* 数据源名称
	*/
    @Schema(description="数据源名称")
    private String name;

	/**
	* jdbcurl
	*/
    @Schema(description="jdbcurl")
    private String url;

	/**
	* 用户名
	*/
    @Schema(description="用户名")
    private String username;

	/**
	* 密码(加密)
	*/
    @Schema(description="密码(加密)")
    private String password;

	/**
	* 数据库类型: mysql, postgresql, oracle, sqlserver等
	*/
    @Schema(description="数据库类型: mysql, postgresql, oracle, sqlserver等")
    private String dsType;

	/**
	* 配置类型
	*/
    @Schema(description="配置类型")
    private String confType;

	/**
	* 数据库名称
	*/
    @Schema(description="数据库名称")
    private String dsName;

	/**
	* 实例
	*/
    @Schema(description="实例")
    private String instance;

	/**
	* 端口
	*/
    @Schema(description="端口")
    private Integer port;

	/**
	* 主机
	*/
    @Schema(description="主机")
    private String host;

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

	/**
	* 删除标记，0正常；1删除
	*/
    @TableLogic
	@TableField(fill = FieldFill.INSERT)
    @Schema(description="删除标记，0正常；1删除")
    private String delFlag;
}