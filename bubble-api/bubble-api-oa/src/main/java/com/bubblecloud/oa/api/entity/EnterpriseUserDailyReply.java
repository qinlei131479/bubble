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
 * 汇报回复，对应 eb_enterprise_user_daily_reply。
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
	private Long id;

	private Long pid;

	@TableField("daily_id")
	private Long dailyId;

	private String uid;

	private String content;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	@TableField("deleted_at")
	private LocalDateTime deletedAt;

}
