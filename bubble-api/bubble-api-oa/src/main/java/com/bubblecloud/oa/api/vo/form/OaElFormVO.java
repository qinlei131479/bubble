package com.bubblecloud.oa.api.vo.form;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 与 PHP {@code elForm} 对齐，供 form-create 使用。
 * @author qinlei
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "表单构建数据")
public class OaElFormVO {

	@Schema(description = "标题")
	private String title;

	@Schema(description = "HTTP 方法")
	private String method;

	@Schema(description = "提交路径（相对 admin 网关）")
	private String action;

	@Schema(description = "form-create 规则数组")
	private JsonNode rule;

}
