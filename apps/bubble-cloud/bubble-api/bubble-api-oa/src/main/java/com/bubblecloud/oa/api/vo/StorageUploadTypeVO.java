package com.bubblecloud.oa.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 当前上传方式（PHP {@code GET ent/config/storage/config}）。
 *
 * @author qinlei
 * @date 2026/4/4 12:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "上传方式配置")
public class StorageUploadTypeVO {

	@Schema(description = "存储类型：1 本地 2 七牛 3 OSS 4 COS …")
	private Integer type;

}
