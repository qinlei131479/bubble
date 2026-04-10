package com.bubblecloud.oa.api.vo.message;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * {@code GET ent/system/message/cate} 单项（含分类字段 + 未读数），对齐 PHP {@code getMessageCateCount}。
 *
 * @author qinlei
 * @date 2026/4/5
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "消息分类与未读数")
public class MessageCategoryCountVO {

	@Schema(description = "分类ID")
	private Long id;

	@Schema(description = "父级ID")
	private Integer pid;

	@Schema(description = "分类名称")
	private String cateName;

	@Schema(description = "路径")
	private String path;

	@Schema(description = "排序")
	private Integer sort;

	@Schema(description = "图标")
	private String pic;

	@Schema(description = "是否展示")
	private Integer isShow;

	@Schema(description = "站内展示")
	private Integer uniShow;

	@Schema(description = "层级")
	private Integer level;

	@Schema(description = "展示标签（与 cate_name 相同）")
	private String label;

	@Schema(description = "选项值（与 id 相同）")
	private Long value;

	@Schema(description = "未读数量")
	private long count;

}
