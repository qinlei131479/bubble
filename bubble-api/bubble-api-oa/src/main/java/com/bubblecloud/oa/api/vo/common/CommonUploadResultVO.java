package com.bubblecloud.oa.api.vo.common;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * POST /ent/common/upload 成功时的 data（与 PHP AttachService::upload 返回一致）。
 *
 * @author qinlei
 * @date 2026/4/4 18:00
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "通用上传结果")
public class CommonUploadResultVO {

	@Schema(description = "相对路径")
	private String src;

	@Schema(description = "完整可访问 URL（含站点域名）")
	private String url;

	@Schema(description = "附件主键")
	private Integer attachId;

	@Schema(description = "同 attachId")
	private Integer id;

	@Schema(description = "文件大小")
	private String size;

	@Schema(description = "原始文件名")
	private String name;

}
