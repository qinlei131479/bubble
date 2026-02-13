package com.bubblecloud.agi.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.bubblecloud.common.mybatis.base.Req;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 知识库
 *
 * @author Rampart
 * @date 2026-02-13 09:30:12
 */
@Data
@TableName("knowledge_bases")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "知识库")
public class KnowledgeBases extends Req<KnowledgeBases> {


	/**
	* 主键ID
	*/
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description="主键ID")
    private Long id;

	/**
	* 数据库ID
	*/
    @Schema(description="数据库ID")
    private String dbId;

	/**
	* 知识库名称
	*/
    @Schema(description="知识库名称")
    private String name;

	/**
	* 知识库描述
	*/
    @Schema(description="知识库描述")
    private String description;

	/**
	* 知识库类型
	*/
    @Schema(description="知识库类型")
    private String kbType;

	/**
	* 向量信息
	*/
    @Schema(description="向量信息")
    private String embedInfo;

	/**
	* 大模型信息
	*/
    @Schema(description="大模型信息")
    private String llmInfo;

	/**
	* 查询参数
	*/
    @Schema(description="查询参数")
    private String queryParams;

	/**
	* 扩展参数
	*/
    @Schema(description="扩展参数")
    private String additionalParams;

	/**
	* 分享配置
	*/
    @Schema(description="分享配置")
    private String shareConfig;
 
	/**
	* mindmap
	*/
    @Schema(description="mindmap")
    private String mindmap;

	/**
	* 样例问题
	*/
    @Schema(description="样例问题")
    private String sampleQuestions;

	/**
	* 创建时间
	*/
    @Schema(description="创建时间")
    private LocalDateTime createdAt;

	/**
	* 更新时间
	*/
    @Schema(description="更新时间")
    private LocalDateTime updatedAt;
}