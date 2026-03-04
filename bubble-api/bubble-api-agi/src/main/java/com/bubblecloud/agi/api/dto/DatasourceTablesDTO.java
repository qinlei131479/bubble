package com.bubblecloud.agi.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 获取数据库表列表请求DTO
 *
 * @author BubbleCloud
 * @date 2026-03-02
 */
@Data
@Schema(description = "获取数据库表列表请求")
public class DatasourceTablesDTO {

    /**
     * 数据源类型
     */
    @Schema(description = "数据源类型", example = "mysql")
    private String dsType;

    /**
     * 主机
     */
    @Schema(description = "主机", example = "localhost")
    private String host;

    /**
     * 端口
     */
    @Schema(description = "端口", example = "3306")
    private Integer port;

    /**
     * 用户名
     */
    @Schema(description = "用户名", example = "root")
    private String username;

    /**
     * 密码
     */
    @Schema(description = "密码")
    private String password;

    /**
     * 数据库名称
     */
    @Schema(description = "数据库名称", example = "test_db")
    private String dsName;

    /**
     * Schema名称（部分数据库需要）
     */
    @Schema(description = "Schema名称")
    private String dbSchema;

    /**
     * 额外JDBC参数
     */
    @Schema(description = "额外JDBC参数", example = "useSSL=false&serverTimezone=UTC")
    private String extraJdbc;

    /**
     * 超时时间（秒）
     */
    @Schema(description = "超时时间（秒）", example = "30")
    private Integer timeout;

    /**
     * Oracle连接模式：service_name 或 sid
     */
    @Schema(description = "Oracle连接模式")
    private String mode;
}
