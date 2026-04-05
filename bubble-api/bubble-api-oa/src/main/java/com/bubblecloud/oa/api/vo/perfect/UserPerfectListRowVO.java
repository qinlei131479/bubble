package com.bubblecloud.oa.api.vo.perfect;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 邀请完善档案列表行 VO。
 *
 * @author qinlei
 * @date 2026/4/5
 */
@Data
@Schema(description = "邀请完善档案记录行")
public class UserPerfectListRowVO {

	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "发起人用户ID")
	private Integer creator;

	@Schema(description = "被邀请用户ID")
	private Integer userId;

	@Schema(description = "企业ID")
	private Long entid;

	@Schema(description = "用户UID")
	private String uid;

	@Schema(description = "名片/员工 admin.id")
	private Integer cardId;

	@Schema(description = "唯一标识")
	private String uniqued;

	@Schema(description = "邀请总次数")
	private Integer total;

	@Schema(description = "已使用次数")
	private Integer used;

	@Schema(description = "状态")
	private Integer status;

	@Schema(description = "类型")
	private Integer types;

	@Schema(description = "失效时间")
	private LocalDateTime failTime;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

	@Schema(description = "企业摘要")
	private UserPerfectEnterpriseVO enterprise;

}
