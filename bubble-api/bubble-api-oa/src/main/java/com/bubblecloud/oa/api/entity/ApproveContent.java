package com.bubblecloud.oa.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bubblecloud.common.mybatis.base.Req;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 审批申请内容表，对应 eb_approve_content 表。
 *
 * @author qinlei
 * @date 2026/4/7 17:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "审批申请内容")
@TableName("eb_approve_content")
public class ApproveContent extends Req<ApproveContent> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@TableField("user_id")
	private Long userId;

	@TableField("card_id")
	private Long cardId;

	@TableField("approve_id")
	private Long approveId;

	@TableField("apply_id")
	private Long applyId;

	private String title;

	private String info;

	@Schema(description = "表单值 JSON 文本")
	private String value;

	private Integer required;

	@TableField("`types`")
	private String types;

	private String symbol;

	private String content;

	private String props;

	private String options;

	private String config;

	private String uniqued;

	private Integer sort;

}
