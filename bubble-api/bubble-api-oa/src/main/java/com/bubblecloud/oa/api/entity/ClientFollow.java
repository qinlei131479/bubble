package com.bubblecloud.oa.api.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bubblecloud.common.mybatis.base.Req;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 客户跟进记录，对应 eb_client_follow 表。
 *
 * @author qinlei
 * @date 2026/4/3 10:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "客户跟进")
@TableName("eb_client_follow")
public class ClientFollow extends Req<ClientFollow> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "客户ID")
	private Integer eid;

	@Schema(description = "用户ID")
	private Integer userId;

	@Schema(description = "说明内容")
	private String content;

	@Schema(description = "类型：0说明；1提醒")
	private Integer types;

	@Schema(description = "提醒时间")
	private LocalDateTime time;

	@Schema(description = "定时任务唯一值")
	private String uniqued;

	@Schema(description = "状态")
	private Integer status;

	@Schema(description = "删除时间")
	private LocalDateTime deletedAt;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

	@Schema(description = "跟进版本")
	private Integer followVersion;

}
