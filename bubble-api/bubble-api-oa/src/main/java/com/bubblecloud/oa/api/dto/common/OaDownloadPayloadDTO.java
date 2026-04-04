package com.bubblecloud.oa.api.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 下载签名载荷（HMAC）。字段名使用 camelCase，与 PHP {@code json_encode($param)} 键名一致。
 *
 * @author qinlei
 * @date 2026/4/4 18:00
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "下载签名载荷")
public class OaDownloadPayloadDTO {

	@Schema(description = "db=附件表；local=站点静态模板")
	private String type;

	@Schema(description = "附件 id 或云文件标识")
	private String fileId;

	@Schema(description = "版本号")
	private Integer version;

	@Schema(description = "是否文件夹（网盘）")
	private Boolean folder;

	@Schema(description = "local 类型下的相对路径")
	private String url;

	@Schema(description = "下载展示文件名")
	private String name;

}
