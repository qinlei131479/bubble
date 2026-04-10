package com.bubblecloud.oa.api.vo.frame;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 企业内人员与部门关联查询行（用于组装人员树）。
 *
 * @author qinlei
 * @date 2026/3/29 下午4:00
 */
@Data
@Schema(description = "部门人员关联行")
public class FrameAssistUserRowVO {

	@Schema(description = "部门 ID")
	private Long frameId;

	private Long id;

	private String name;

	private String avatar;

	private String phone;

	private Integer job;

	private String uid;

}
