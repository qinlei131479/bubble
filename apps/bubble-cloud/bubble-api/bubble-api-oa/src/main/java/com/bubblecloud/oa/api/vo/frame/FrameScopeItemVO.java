package com.bubblecloud.oa.api.vo.frame;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理范围部门简要项。
 *
 * @author qinlei
 * @date 2026/3/29 下午4:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "管理范围部门项")
public class FrameScopeItemVO {

	private Long id;

	private String name;

}
