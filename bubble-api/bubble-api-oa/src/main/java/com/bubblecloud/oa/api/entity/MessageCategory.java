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
 * 消息分类，对应 eb_message_category。
 *
 * @author qinlei
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "消息分类")
@TableName("eb_message_category")
public class MessageCategory extends Req<MessageCategory> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	private Long id;

	private Integer pid;

	@TableField("cate_name")
	private String cateName;

	private String path;

	private Integer sort;

	private String pic;

	@TableField("is_show")
	private Integer isShow;

	@TableField("uni_show")
	private Integer uniShow;

	private Integer level;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

}
