package com.bubblecloud.oa.api.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.baomidou.mybatisplus.annotation.*;
import com.bubblecloud.common.mybatis.base.Req;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 晋升数据项，对应 eb_promotion_data 表。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "晋升数据项")
@TableName("eb_promotion_data")
public class PromotionData extends Req<PromotionData> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "晋升配置ID")
	private Long pid;

	@Schema(description = "企业ID")
	private Long entid;

	@Schema(description = "条件名称")
	private String name;

	@Schema(description = "条件说明")
	private String content;

	@Schema(description = "标准值")
	private String standard;

	@Schema(description = "排序")
	private Integer sort;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

	@Schema(description = "删除时间")
	@TableLogic
	private LocalDateTime deletedAt;

	@TableField(exist = false)
	@Schema(description = "排序后的ID列表（从前到后对应 sort 1,2,3...）")
	private List<Long> ids;
}
