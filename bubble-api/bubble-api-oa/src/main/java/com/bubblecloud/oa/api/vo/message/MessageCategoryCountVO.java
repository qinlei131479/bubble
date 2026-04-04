package com.bubblecloud.oa.api.vo.message;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

/**
 * {@code GET ent/system/message/cate} 单项（含分类字段 + 未读数），对齐 PHP {@code getMessageCateCount}。
 *
 * @author qinlei
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MessageCategoryCountVO {

	private Long id;

	private Integer pid;

	private String cateName;

	private String path;

	private Integer sort;

	private String pic;

	private Integer isShow;

	private Integer uniShow;

	private Integer level;

	/** 与 {@code cate_name} 相同（PHP select 别名） */
	private String label;

	/** 与 {@code id} 相同（PHP select 别名） */
	private Long value;

	private long count;

}
