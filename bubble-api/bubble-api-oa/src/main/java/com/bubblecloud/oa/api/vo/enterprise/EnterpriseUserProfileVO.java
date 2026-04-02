package com.bubblecloud.oa.api.vo.enterprise;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 当前登录用户在企业侧扩展信息（对齐 PHP {@code EnterpriseUserController::userInfo}）。
 *
 * @author qinlei
 * @date 2026/3/29 下午5:30
 */
@Data
@Schema(description = "企业用户 profile")
public class EnterpriseUserProfileVO {

	@JsonProperty("id")
	private Long id;

	@JsonProperty("uid")
	private String uid;

	@JsonProperty("name")
	private String name;

	@JsonProperty("avatar")
	private String avatar;

	@JsonProperty("phone")
	private String phone;

	@JsonProperty("job")
	private Integer job;

	@JsonProperty("status")
	private Integer status;

	@JsonProperty("account")
	private String account;

	@JsonProperty("entIds")
	private List<Integer> entIds;

	@JsonProperty("job_id")
	private Integer jobId;

	@JsonProperty("maxScore")
	private Integer maxScore;

	@JsonProperty("compute_mode")
	private Integer computeMode;

}
