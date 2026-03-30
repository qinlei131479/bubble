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
 * 日程类型，对应 eb_schedule_type。
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
	private Long id;

	@TableField("user_id")
	private Long userId;

	private String uid;

	private Integer entid;

	private String name;

	private Integer sort;

	private String color;

	private String info;

	@TableField("is_public")
	private Integer isPublic;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

}
