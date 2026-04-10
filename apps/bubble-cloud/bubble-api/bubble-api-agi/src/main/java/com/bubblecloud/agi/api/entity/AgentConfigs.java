package com.bubblecloud.agi.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.bubblecloud.common.mybatis.base.Req;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 智能体配置表
 *
 * @author rampart
 * @date 2026-02-11 10:56:22
 */
@Data
@TableName("agent_configs")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "智能体配置表")
public class AgentConfigs extends Req<AgentConfigs> {


	/**
	* 主键ID
	*/
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description="主键ID")
    private Long id;

	/**
	* 智能体ID
	*/
    @Schema(description="智能体ID")
    private String agentId;

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
	* 知识库图标
	*/
    @Schema(description="知识库图标")
    private String icon;

	/**
	* 知识库图片
	*/
    @Schema(description="知识库图片")
    private String pics;

	/**
	* 案例信息
	*/
    @Schema(description="案例信息")
    private String examples;

	/**
	* json配置信息
	*/
    @Schema(description="json配置信息")
    private String configJson;

	/**
	* 是否默认，0否1是
	*/
    @Schema(description="是否默认，0否1是")
    private String defaultFlag;

	/**
	* 创建人
	*/
	@TableField(fill = FieldFill.INSERT)
    @Schema(description="创建人")
    private String createdBy;

	/**
	* 更新人
	*/
	@TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description="更新人")
    private String updatedBy;

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
