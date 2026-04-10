package com.bubblecloud.oa.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 备忘录保存/修改请求（对齐 PHP postMore 字段）。
 *
 * @author qinlei
 * @date 2026/4/2 10:00
 */
@Data
@Schema(description = "备忘录保存参数")
public class UserMemorialSaveDTO {

	@Schema(description = "分类ID，0 表示由服务端填入默认分类")
	private Integer pid;

	@Schema(description = "标题")
	private String title;

	@Schema(description = "内容")
	private String content;

}
