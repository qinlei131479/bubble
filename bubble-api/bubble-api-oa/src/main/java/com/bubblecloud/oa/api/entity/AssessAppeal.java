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
 * 绩效申诉，对应 eb_assess_appeal 表。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "绩效申诉")
@TableName("eb_assess_appeal")
public class AssessAppeal extends Req<AssessAppeal> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "绩效考核ID")
	private Long assessId;

	@Schema(description = "企业ID")
	private Long entid;

	@Schema(description = "申诉人ID(admin)")
	private Long userId;

	@Schema(description = "申诉内容")
	private String content;

	@Schema(description = "处理结果：0待处理 1通过 2驳回")
	private Integer result;

	@Schema(description = "处理意见")
	private String opinion;

	@Schema(description = "处理人ID")
	private Long handleUserId;

	@Schema(description = "处理时间")
	private LocalDateTime handleTime;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

}
