package com.bubblecloud.agi.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.bubblecloud.common.mybatis.base.Req;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 评估结果明细
 *
 * @author Rampart
 * @date 2026-02-13 16:52:13
 */
@Data
@TableName("evaluation_result_details")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "评估结果明细")
public class EvaluationResultDetails extends Req<EvaluationResultDetails> {


	/**
	* 评估结果明细ID
	*/
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description="评估结果明细ID")
    private Long id;

	/**
	* 任务ID
	*/
    @Schema(description="任务ID")
    private String taskId;

	/**
	* 查询索引
	*/
    @Schema(description="查询索引")
    private Integer queryIndex;

	/**
	* 查询内容
	*/
    @Schema(description="查询内容")
    private String queryText;

	/**
	* 正确分块IDS
	*/
    @Schema(description="正确分块IDS")
    private String goldChunkIds;

	/**
	* 正确答案
	*/
    @Schema(description="正确答案")
    private String goldAnswer;

	/**
	* 生成答案
	*/
    @Schema(description="生成答案")
    private String generatedAnswer;

	/**
	* 检索到的块
	*/
    @Schema(description="检索到的块")
    private String retrievedChunks;

	/**
	* 指标
	*/
    @Schema(description="指标")
    private String metrics;
}