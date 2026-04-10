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
 * 消息分类，对应 eb_message_category 表。
 *
 * @author qinlei
 * @date 2026/4/5
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "消息分类")
@TableName("eb_message_category")
public class MessageCategory extends Req<MessageCategory> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "父级分类ID")
	private Integer pid;

	@Schema(description = "分类名称")
	@TableField("cate_name")
	private String cateName;

	@Schema(description = "路径")
	private String path;

	@Schema(description = "排序")
	private Integer sort;

	@Schema(description = "图标")
	private String pic;

	@Schema(description = "是否展示")
	@TableField("is_show")
	private Integer isShow;

	@Schema(description = "站内展示")
	@TableField("uni_show")
	private Integer uniShow;

	@Schema(description = "层级")
	private Integer level;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

}
