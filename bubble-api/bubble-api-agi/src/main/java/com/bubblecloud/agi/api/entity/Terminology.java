package com.bubblecloud.agi.api.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.*;
import com.bubblecloud.common.mybatis.base.Req;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 实体类：术语表
 *
 * @author Rampart Qin
 * @date   2026/02/11 22:35
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "术语表")
@TableName("terminology")
public class Terminology extends Req<Terminology> {

    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @TableId(type = IdType.INPUT)
    @Schema(description = "主键")
	private Long id;
    /**
     * 父级ID
     */
    @Schema(description = "父级ID")
	private Long parentId;
    /**
     * 术语名称
     */
    @Schema(description = "术语名称")
	private String word;
    /**
     * 术语描述
     */
    @Schema(description = "术语描述")
	private String description;
    /**
     * 是否指定数据源，0否；1是
     */
    @Schema(description = "是否指定数据源，0否；1是")
	private Integer specificDs;
    /**
     * 术语向量数据（pgvector VECTOR 类型，支持动态维度）
     */
    @Schema(description = "术语向量数据（pgvector VECTOR 类型，支持动态维度）")
	private String embedding;
    /**
     * 数据源ID列表(JSON)
     */
    @Schema(description = "数据源ID列表(JSON)")
	private String datasourceIds;
    /**
     * 是否启用，0否；1是
     */
    @Schema(description = "是否启用，0否；1是")
	private Integer enabledFlag;
    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
	@TableField(fill = FieldFill.INSERT)
	private LocalDateTime createTime;
    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
	@TableField(fill = FieldFill.INSERT_UPDATE)
	private LocalDateTime updateTime;
}
