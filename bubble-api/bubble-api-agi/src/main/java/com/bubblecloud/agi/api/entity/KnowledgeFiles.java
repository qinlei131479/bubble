package com.bubblecloud.agi.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.bubblecloud.common.mybatis.base.Req;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 知识库文件
 *
 * @author Rampart
 * @date 2026-02-13 09:33:20
 */
@Data
@TableName("knowledge_files")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "知识库文件")
public class KnowledgeFiles extends Req<KnowledgeFiles> {


	/**
	* 主键ID
	*/
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description="主键ID")
    private Long id;

	/**
	* 文件ID
	*/
    @Schema(description="文件ID")
    private String fileId;

	/**
	* 数据库ID
	*/
    @Schema(description="数据库ID")
    private String dbId;

	/**
	* 父ID
	*/
    @Schema(description="父ID")
    private Long parentId;

	/**
	* 文件名称
	*/
    @Schema(description="文件名称")
    private String filename;

	/**
	* 文件原名
	*/
    @Schema(description="文件原名")
    private String originalFilename;

	/**
	* 文件类型
	*/
    @Schema(description="文件类型")
    private String fileType;

	/**
	* 文件路径
	*/
    @Schema(description="文件路径")
    private String path;

	/**
	* minio地址
	*/
    @Schema(description="minio地址")
    private String minioUrl;

	/**
	* markdown文件
	*/
    @Schema(description="markdown文件")
    private String markdownFile;

	/**
	* 状态
	*/
    @Schema(description="状态")
    private String status;

	/**
	* 内容hash值
	*/
    @Schema(description="内容hash值")
    private String contentHash;

	/**
	* 文件大小
	*/
    @Schema(description="文件大小")
    private Long fileSize;

	/**
	* 内容类型
	*/
    @Schema(description="内容类型")
    private String contentType;

	/**
	* 处理参数
	*/
    @Schema(description="处理参数")
    private String processingParams;

	/**
	* 是否目录，0否1是
	*/
    @Schema(description="是否目录，0否1是")
    private String folderFlag;

	/**
	* 错误信息
	*/
    @Schema(description="错误信息")
    private String errorMessage;

	/**
	* 创建人
	*/
    @Schema(description="创建人")
	@TableField(fill = FieldFill.INSERT)
    private String createdBy;

	/**
	* 更新人
	*/
    @Schema(description="更新人")
	@TableField(fill = FieldFill.INSERT_UPDATE)
    private String updatedBy;

	/**
	* 创建时间
	*/
    @Schema(description="创建时间")
	@TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

	/**
	* 更新时间
	*/
    @Schema(description="更新时间")
	@TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}