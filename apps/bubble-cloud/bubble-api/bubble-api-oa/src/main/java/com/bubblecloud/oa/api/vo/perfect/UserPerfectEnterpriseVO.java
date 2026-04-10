package com.bubblecloud.oa.api.vo.perfect;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 邀请记录中的企业摘要 VO。
 *
 * @author qinlei
 * @date 2026/4/5
 */
@Data
@Schema(description = "邀请记录中的企业摘要")
public class UserPerfectEnterpriseVO {

	@Schema(description = "企业ID")
	private Long id;

	@Schema(description = "企业名称")
	private String enterpriseName;

	@Schema(description = "企业Logo")
	private String logo;

}
