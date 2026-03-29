package com.bubblecloud.oa.api.vo.company;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户主部门简要（PHP {@code userFrame} 返回的 frame 对象简化版）。
 *
 * @author qinlei
 * @date 2026/3/29 下午5:30
 */
@Data
@Schema(description = "用户部门简要")
public class UserFrameBriefVO {

	@JsonProperty("id")
	private Long id;

	@JsonProperty("name")
	private String name;

	@JsonProperty("entid")
	private Long entid;

}
