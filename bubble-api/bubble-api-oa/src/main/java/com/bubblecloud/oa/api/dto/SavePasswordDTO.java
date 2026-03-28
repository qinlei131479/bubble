package com.bubblecloud.oa.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 修改密码（兼容 PHP ent/user/savePassword）。
 */
@Data
@Schema(description = "修改密码参数")
public class SavePasswordDTO {

	@NotBlank
	@Schema(description = "手机号")
	private String phone;

	@NotBlank
	@Schema(description = "新密码")
	private String password;

}
