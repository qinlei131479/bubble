package com.bubblecloud.oa.api.vo.company;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 档案中的职位对象（对齐 PHP job 关联）。
 *
 * @author qinlei
 * @date 2026/4/5
 */
@Data
@Schema(description = "员工档案职位")
public class CompanyCardJobVO {

	@Schema(description = "职位ID")
	private Long id;

	@Schema(description = "职位名称")
	private String name;

	@Schema(description = "职位描述")
	private String describe;

}
