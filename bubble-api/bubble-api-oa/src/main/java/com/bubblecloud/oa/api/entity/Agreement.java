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
 * 用户协议，对应 eb_agreement 表。
 *
 * @author qinlei
 * @date 2026/3/30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "协议")
@TableName("eb_agreement")
public class Agreement extends Req<Agreement> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Integer id;

	@Schema(description = "协议标识")
	private String ident;

	@Schema(description = "标题")
	private String title;

	@Schema(description = "内容")
	private String content;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

}
