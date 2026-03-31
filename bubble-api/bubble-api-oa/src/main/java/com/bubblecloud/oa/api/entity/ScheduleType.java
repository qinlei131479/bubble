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
 * 日程类型，对应 eb_schedule_type 表。
 *
 * @author qinlei
 * @date 2026/3/29 18:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "日程类型")
@TableName("eb_schedule_type")
public class ScheduleType extends Req<ScheduleType> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "用户ID（admin 主键）")
	@TableField("user_id")
	private Long userId;

	@Schema(description = "用户 UID")
	private String uid;

	@Schema(description = "企业ID")
	private Integer entid;

	@Schema(description = "类型名称")
	private String name;

	@Schema(description = "排序")
	private Integer sort;

	@Schema(description = "颜色")
	private String color;

	@Schema(description = "说明")
	private String info;

	@Schema(description = "是否公开")
	@TableField("is_public")
	private Integer isPublic;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

}
