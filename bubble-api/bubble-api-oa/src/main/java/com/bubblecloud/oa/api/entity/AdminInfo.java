package com.bubblecloud.oa.api.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bubblecloud.common.mybatis.base.Req;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 员工档案扩展，对应 eb_admin_info 表（主键与 eb_admin.id 一致）。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "员工档案扩展")
@TableName("eb_admin_info")
public class AdminInfo extends Req<AdminInfo> {

	private static final long serialVersionUID = 1L;

	@TableId
	@Schema(description = "主键ID（与 eb_admin.id 一致）")
	private Long id;

	@Schema(description = "用户UID")
	private String uid;

	@Schema(description = "邮箱")
	private String email;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

	@Schema(description = "删除时间")
	private LocalDateTime deletedAt;

}
