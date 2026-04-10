package com.bubblecloud.oa.api.vo;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 仅返回路径（PHP {@code fileUpload} → {@code data.src}）。
 *
 * @author qinlei
 * @date 2026/4/4 12:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "附件路径")
public class AttachFileSrcVO {

	@Schema(description = "相对路径")
	private String src;

}
