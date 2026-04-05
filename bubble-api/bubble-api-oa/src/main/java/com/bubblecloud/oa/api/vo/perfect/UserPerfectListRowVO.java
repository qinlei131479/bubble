package com.bubblecloud.oa.api.vo.perfect;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "邀请完善档案记录行")
public class UserPerfectListRowVO {

	private Long id;

	private Integer creator;

	private Integer userId;

	private Long entid;

	private String uid;

	private Integer cardId;

	private String uniqued;

	private Integer total;

	private Integer used;

	private Integer status;

	private Integer types;

	private LocalDateTime failTime;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	private UserPerfectEnterpriseVO enterprise;

}
