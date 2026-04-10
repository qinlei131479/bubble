package com.bubblecloud.agi.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 数据库表信息DTO
 *
 * @author BubbleCloud
 * @date 2026-03-02
 */
@Data
@Schema(description = "数据库表信息")
public class TableInfoVO {

    /**
     * 表名称
     */
    @Schema(description = "表名称")
    private String tableName;

    /**
     * 表注释
     */
    @Schema(description = "表注释")
    private String tableComment;

    /**
     * 表类型
     */
    @Schema(description = "表类型")
    private String tableType;

    /**
     * 行数
     */
    @Schema(description = "行数")
    private Long tableRows;
}
