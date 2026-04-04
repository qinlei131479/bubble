package com.bubblecloud.oa.api.vo.common;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GET /ent/common/download_url 返回。
 *
 * @author qinlei
 * @date 2026/4/4 18:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "带签名的下载地址")
public class CommonDownloadUrlVO {

	@Schema(description = "完整下载 URL（含 signature）")
	private String downloadUrl;

}
