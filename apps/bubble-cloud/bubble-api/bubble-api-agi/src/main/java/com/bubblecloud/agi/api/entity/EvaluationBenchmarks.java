package com.bubblecloud.agi.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.bubblecloud.common.mybatis.base.Req;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 评估基准
 *
 * @author Rampart
 * @date 2026-02-13 16:51:12
 */
@Data
@TableName("evaluation_benchmarks")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "评估基准")
public class EvaluationBenchmarks extends Req<EvaluationBenchmarks> {


	/**
	* 评估指标ID
	*/
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description="评估指标ID")
    private Long id;

	/**
	* 基准ID
	*/
    @Schema(description="基准ID")
    private String benchmarkId;

	/**
	* 数据库ID
	*/
    @Schema(description="数据库ID")
    private String dbId;

	/**
	* 基准名称
	*/
    @Schema(description="基准名称")
    private String name;

	/**
	* 描述
	*/
    @Schema(description="描述")
    private String description;

	/**
	* 问题数量
	*/
    @Schema(description="问题数量")
    private Integer questionNum;

	/**
	* 分块
	*/
    @Schema(description="分块")
    private String hasGoldChunks;

	/**
	* 回答
	*/
    @Schema(description="回答")
    private String hasGoldAnswers;

	/**
	* 文件路径
	*/
    @Schema(description="文件路径")
    private String dataFilePath;

	/**
	* 创建人
	*/
    @Schema(description="创建人")
    private String createdBy;

	/**
	* 创建时间
	*/
	@TableField(fill = FieldFill.INSERT)
    @Schema(description="创建时间")
    private LocalDateTime createdAt;

	/**
	* 更新时间
	*/
	@TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description="更新时间")
    private LocalDateTime updatedAt;
}