package com.bubblecloud.oa.api.vo.frame;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 部门详情（对齐 PHP getDepartmentInfo）。
 *
 * @author qinlei
 * @date 2026/3/29 下午4:00
 */
@Data
@Schema(description = "部门详情")
public class FrameDetailVO {

	private Long id;

	private Long userId;

	private Long entid;

	private Integer pid;

	private Integer roleId;

	private String name;

	private String path;

	private String introduce;

	private Integer sort;

	private Integer userCount;

	private Integer userSingleCount;

	private Integer isShow;

	private Integer level;

}
