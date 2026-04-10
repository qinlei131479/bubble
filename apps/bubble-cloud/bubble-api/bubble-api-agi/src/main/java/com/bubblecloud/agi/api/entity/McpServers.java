package com.bubblecloud.agi.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.bubblecloud.common.mybatis.base.Req;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * MCP服务表
 *
 * @author Rampart
 * @date 2026-02-12 18:18:39
 */
@Data
@TableName("mcp_servers")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "MCP服务表")
public class McpServers extends Req<McpServers> {


	/**
	* 主键ID
	*/
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description="主键ID")
    private Long id;

	/**
	* 服务器名称（唯一标识）
	*/
    @Schema(description="服务器名称（唯一标识）")
    private String name;

	/**
	* 描述
	*/
    @Schema(description="描述")
    private String description;

	/**
	* 传输类型：sse/streamable_http/stdio
	*/
    @Schema(description="传输类型：sse/streamable_http/stdio")
    private String transport;

	/**
	* 服务器 URL（sse/streamable_http）
	*/
    @Schema(description="服务器 URL（sse/streamable_http）")
    private String url;

	/**
	* 命令（stdio）
	*/
    @Schema(description="命令（stdio）")
    private String command;

	/**
	* 命令参数数组（stdio）
	*/
    @Schema(description="命令参数数组（stdio）")
    private String args;

	/**
	* HTTP 请求头
	*/
    @Schema(description="HTTP 请求头")
    private String headers;

	/**
	* HTTP 超时时间（秒）
	*/
    @Schema(description="HTTP 超时时间（秒）")
    private Integer timeout;

	/**
	* SSE 读取超时（秒）
	*/
    @Schema(description="SSE 读取超时（秒）")
    private Integer sseReadTimeout;

	/**
	* 标签数组
	*/
    @Schema(description="标签数组")
    private String tags;

	/**
	* 图标（emoji）
	*/
    @Schema(description="图标（emoji）")
    private String icon;

	/**
	* 是否启用：0=否,1=是
	*/
    @Schema(description="是否启用：0=否,1=是")
    private String enabledFlag;

	/**
	* 禁用的工具名称列表
	*/
    @Schema(description="禁用的工具名称列表")
    private String disabledTools;

	/**
	* 创建人用户名
	*/
    @Schema(description="创建人用户名")
	@TableField(fill = FieldFill.INSERT)
    private String createBy;

	/**
	* 修改人用户名
	*/
    @Schema(description="修改人用户名")
	@TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

	/**
	* 创建时间
	*/
    @Schema(description="创建时间")
	@TableField(fill = FieldFill.INSERT)
    private LocalDateTime updateTime;

	/**
	* 更新时间
	*/
    @Schema(description="更新时间")
	@TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime createTime;
}