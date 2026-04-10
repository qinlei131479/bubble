package com.bubblecloud.oa.api.vo.auth;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

/**
 * 登录信息中用户部门关联行（对齐 PHP frames 数组元素）。
 *
 * @author qinlei
 * @date 2026/3/29 下午5:00
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class FrameAssistLoginRowVO {

	private Long id;

	private Integer entid;

	private Integer frameId;

	private Long userId;

	private Integer isMastart;

	private Integer isAdmin;

	private Long superiorUid;

	private FrameNameRefVO frame;

}
