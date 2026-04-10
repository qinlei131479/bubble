package com.bubblecloud.oa.api.vo.enterprise;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 企业所属管理员简要信息（PHP 企业详情 user 关联）。
 *
 * @author qinlei
 * @date 2026/3/29 下午5:30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "企业所属用户")
public class EnterpriseOwnerUserVO {

	@JsonProperty("uid")
	@Schema(description = "用户 UID")
	private String uid;

	@JsonProperty("name")
	@Schema(description = "姓名")
	private String name;

}
