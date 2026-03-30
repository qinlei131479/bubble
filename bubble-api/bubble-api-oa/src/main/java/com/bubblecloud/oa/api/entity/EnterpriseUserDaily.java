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
 * 工作汇报记录，对应 eb_enterprise_user_daily。
 *
 * @author qinlei
 * @date 2026/3/29 20:30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "工作汇报")
@TableName("eb_enterprise_user_daily")
public class EnterpriseUserDaily extends Req<EnterpriseUserDaily> {

	private static final long serialVersionUID = 1L;

	@TableId(value = "daily_id", type = IdType.AUTO)
	private Long dailyId;

	private Long entid;

	private String uid;

	@TableField("user_id")
	private Long userId;

	private String finish;

	private String plan;

	private String mark;

	private Integer status;

	private Integer types;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

}
