package com.bubblecloud.oa.api.vo.enterprise;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 组织架构 / 通讯录用户列表行。
 *
 * @author qinlei
 * @date 2026/3/29 下午5:30
 */
@Data
@Schema(description = "企业用户列表项")
public class EnterpriseUserListItemVO {

	@JsonProperty("id")
	private Long id;

	@JsonProperty("name")
	private String name;

	@JsonProperty("avatar")
	private String avatar;

	@JsonProperty("phone")
	private String phone;

	@JsonProperty("job")
	private Integer job;

	@JsonProperty("job_name")
	private String jobName;

	@JsonProperty("frame_names")
	private String frameNames;

}
