package com.bubblecloud.oa.api.vo.perfect;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author qinlei
 */
@Data
@Schema(description = "邀请记录中的企业摘要")
public class UserPerfectEnterpriseVO {

	private Long id;

	@Schema(description = "企业名称")
	private String enterpriseName;

	private String logo;

}
