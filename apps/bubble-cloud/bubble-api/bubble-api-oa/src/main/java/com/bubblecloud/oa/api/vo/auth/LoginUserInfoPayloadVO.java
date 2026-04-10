package com.bubblecloud.oa.api.vo.auth;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

/**
 * 登录会话 userInfo（对齐 PHP AdminService::loginInfo 扁平结构，蛇形 JSON）。
 *
 * @author qinlei
 * @date 2026/3/29 下午5:00
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class LoginUserInfoPayloadVO {

	private Long id;

	private String uid;

	private String account;

	private String avatar;

	private String name;

	private String phone;

	private Integer isAdmin;

	private Integer uniOnline;

	private String clientId;

	private String scanKey;

	private String lastIp;

	private Integer loginCount;

	private Integer status;

	private Integer isInit;

	private String language;

	private String mark;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	private List<Object> roles;

	private List<FrameAssistLoginRowVO> frames;

	private RankJobLoginVO job;

	private String realName;

}
