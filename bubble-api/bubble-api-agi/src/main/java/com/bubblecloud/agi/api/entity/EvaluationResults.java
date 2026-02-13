package com.bubblecloud.agi.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.bubblecloud.common.mybatis.base.Req;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 评估结果
 *
 * @author Rampart
 * @date 2026-02-13 16:53:41
 */
@Data
@TableName("evaluation_results")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "评估结果")
public class EvaluationResults extends Req<EvaluationResults> {


	/**
	* 评估结果ID
	*/
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description="评估结果ID")
    private Long id;

	/**
	* 任务ID
	*/
    @Schema(description="任务ID")
    private String taskId;

	/**
	* 数据库ID
	*/
    @Schema(description="数据库ID")
    private String dbId;

	/**
	* 评估基准ID
	*/
    @Schema(description="评估基准ID")
    private String benchmarkId;

	/**
	* 状态
	*/
    @Schema(description="状态")
    private String status;

	/**
	* 检索配置
	*/
    @Schema(description="检索配置")
    private String retrievalConfig;

	/**
	* 指标
	*/
    @Schema(description="指标")
    private String metrics;

	/**
	* 得分
	*/
    @Schema(description="得分")
    private Double overallScore;

	/**
	* 统计数量
	*/
    @Schema(description="统计数量")
    private Integer totalQuestions;

	/**
	* 完成数量
	*/
    @Schema(description="完成数量")
    private Integer completedQuestions;

	/**
	* 完成时间
	*/
    @Schema(description="完成时间")
    private LocalDateTime completedAt;

	/**
	* 创建时间
	*/
	@TableField(fill = FieldFill.INSERT)
    @Schema(description="创建时间")
    private LocalDateTime createdAt;

	/**
	* 创建人
	*/
	@TableField(fill = FieldFill.INSERT)
    @Schema(description="创建人")
    private String createdBy;
}