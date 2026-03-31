package com.bubblecloud.oa.api.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bubblecloud.common.mybatis.base.Req;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 工作汇报回复，对应 eb_enterprise_user_daily_reply 表。
 *
 * @author qinlei
 * @date 2026/3/29 20:30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "汇报回复")
@TableName("eb_enterprise_user_daily_reply")
public class EnterpriseUserDailyReply extends Req<EnterpriseUserDailyReply> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "父回复ID")
	private Long pid;

	@Schema(description = "汇报主记录ID")
	@TableField("daily_id")
	private Long dailyId;

	@Schema(description = "回复人 UID")
	private String uid;

	@Schema(description = "回复内容")
	private String content;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

	@Schema(description = "删除时间")
	@TableField("deleted_at")
	private LocalDateTime deletedAt;

}
