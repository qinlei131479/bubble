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
 * 组合数据项，对应 eb_system_group_data（续费类型等 JSON 配置）。
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "系统组合数据项")
@TableName("eb_system_group_data")
public class SystemGroupData extends Req<SystemGroupData> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键")
	private Long id;

	@TableField("group_id")
	@Schema(description = "组合 ID")
	private Integer groupId;

	@Schema(description = "JSON 数据")
	private String value;

	@Schema(description = "排序")
	private Integer sort;

	@Schema(description = "状态")
	private Integer status;

	@TableField("created_at")
	private LocalDateTime createdAt;

	@TableField("updated_at")
	private LocalDateTime updatedAt;

}
