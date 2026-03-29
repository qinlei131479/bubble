package com.bubblecloud.oa.api.vo.frame;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 部门下主岗人员简要信息（主部门 is_mastart=1）。
 *
 * @author qinlei
 * @date 2026/3/29 下午4:00
 */
@Data
@Schema(description = "部门人员简要")
public class FrameAdminBriefVO {

	private Long id;

	private String name;

	private String avatar;

	private String uid;

}
