package com.bubblecloud.oa.api.vo;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 附件保存/本地上传返回（与 PHP AttachService::save / upload 字段对齐）。
 *
 * @author qinlei
 * @date 2026/4/4 12:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "附件上传结果")
public class AttachUploadResultVO {

	@Schema(description = "访问路径")
	private String src;

	@Schema(description = "同 src")
	private String url;

	@Schema(description = "附件主键")
	private Integer attachId;

	@Schema(description = "同 attach_id")
	private Integer id;

	@Schema(description = "大小")
	private String size;

	@Schema(description = "展示名")
	private String name;

	public static AttachUploadResultVO of(String src, String url, Integer attachId, String size, String name) {
		return new AttachUploadResultVO(src, url, attachId, attachId, size, name);
	}

}
