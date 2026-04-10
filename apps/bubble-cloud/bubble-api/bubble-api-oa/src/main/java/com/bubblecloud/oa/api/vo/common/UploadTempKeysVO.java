package com.bubblecloud.oa.api.vo.common;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GET /ent/common/upload_key 返回（与 PHP 本地/JDOSS 结构字段一致）。
 *
 * @author qinlei
 * @date 2026/4/4 18:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "直传/本地上传配置")
public class UploadTempKeysVO {

	@Schema(description = "上传入口地址")
	private String uploadUrl;

	@Schema(description = "存储类型标识")
	private String type;

	@Schema(description = "访问域名或站点根")
	private String url;

}
