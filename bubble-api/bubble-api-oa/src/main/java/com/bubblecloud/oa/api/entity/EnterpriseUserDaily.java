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
 * 工作汇报，对应 eb_enterprise_user_daily 表。
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
	@Schema(description = "主键 daily_id")
	private Long dailyId;

	@Schema(description = "企业ID")
	private Long entid;

	@Schema(description = "用户 UID")
	private String uid;

	@Schema(description = "员工 admin.id")
	@TableField("user_id")
	private Long userId;

	@Schema(description = "今日完成")
	private String finish;

	@Schema(description = "明日计划")
	private String plan;

	@Schema(description = "备注")
	private String mark;

	@Schema(description = "状态")
	private Integer status;

	@Schema(description = "汇报类型")
	private Integer types;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

}
