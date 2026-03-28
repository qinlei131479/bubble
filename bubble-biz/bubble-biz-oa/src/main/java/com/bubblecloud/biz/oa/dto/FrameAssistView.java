package com.bubblecloud.biz.oa.dto;

import lombok.Data;

/**
 * 用户部门关联查询行（对应 PHP FrameAssistService::getUserFrames）。
 */
@Data
public class FrameAssistView {

	private Long id;

	private Integer entid;

	private Integer frameId;

	private Long userId;

	private Integer isMastart;

	private Integer isAdmin;

	private Long superiorUid;

	private String frameName;

}
