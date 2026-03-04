package com.bubblecloud.agi.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 数据源测试连接响应DTO
 *
 * @author BubbleCloud
 * @date 2026-03-02
 */
@Data
@Schema(description = "数据源测试连接响应")
public class DatasourceTestResultVO {

    /**
     * 是否连接成功
     */
    @Schema(description = "是否连接成功")
    private Boolean connected;

    /**
     * 错误信息（连接失败时返回）
     */
    @Schema(description = "错误信息")
    private String errorMessage;

    /**
     * 数据库名称
     */
    @Schema(description = "数据库名称")
    private String databaseName;

    /**
     * 数据库版本
     */
    @Schema(description = "数据库版本")
    private String databaseVersion;
}
