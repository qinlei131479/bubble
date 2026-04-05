package com.bubblecloud.oa.api.dto.frame;

import lombok.Data;

/**
 * 批量查询用户所属部门（员工档案列表附加）。
 *
 * @author qinlei
 */
@Data
public class FrameAssistCardBatchRow {

	private Long userId;

	private Long frameId;

	private String frameName;

	private Integer userCount;

	private Integer isMastart;

	private Integer isAdmin;

	private Long superiorUid;

}
