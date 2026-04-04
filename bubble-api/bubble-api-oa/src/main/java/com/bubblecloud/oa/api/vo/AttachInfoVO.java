package com.bubblecloud.oa.api.vo;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 附件详情（PHP {@code GET ent/system/attach/info/{id}} 字段别名对齐）。
 *
 * @author qinlei
 * @date 2026/4/4 12:00
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "附件详情")
public class AttachInfoVO {

	@Schema(description = "主键")
	private Integer id;

	@Schema(description = "扩展名")
	private String fileExt;

	@Schema(description = "存储文件名")
	private String fileName;

	@Schema(description = "原始文件名")
	private String realName;

	@Schema(description = "大小")
	private String fileSize;

	@Schema(description = "MIME")
	private String fileType;

	@Schema(description = "访问路径")
	private String fileUrl;

}
