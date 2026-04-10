package com.bubblecloud.oa.api.vo.attendance;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * id + 名称（考勤组详情等通用）。
 *
 * @author qinlei
 * @date 2026/4/2 16:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "ID与名称")
public class OaIdNameVO {

	@Schema(description = "主键")
	private Long id;

	@Schema(description = "名称")
	private String name;

}
