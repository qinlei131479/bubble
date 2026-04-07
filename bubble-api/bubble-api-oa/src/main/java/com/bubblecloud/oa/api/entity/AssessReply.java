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
 * 绩效考核评价/申诉回复，对应 eb_assess_reply 表。
 *
 * @author qinlei
 * @date 2026/4/7 10:30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "绩效考核评价/申诉")
@TableName("eb_assess_reply")
public class AssessReply extends Req<AssessReply> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "考核记录ID")
	@TableField(value = "assessid")
	private Long assessId;

	@Schema(description = "企业ID")
	private Long entid;

	@Schema(description = "用户ID")
	@TableField(value = "user_id")
	private Long userId;

	@Schema(description = "内容")
	private String content;

	@Schema(description = "自身可见 0否 1是")
	@TableField(value = "is_own")
	private Integer isOwn;

	@Schema(description = "类型：0评价 1申诉")
	private Integer types;

	@Schema(description = "申诉状态：0待处理 1已处理 2已拒绝")
	private Integer status;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

}
