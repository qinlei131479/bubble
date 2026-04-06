package com.bubblecloud.oa.api.entity;

import java.math.BigDecimal;
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
 * 绩效考核评分记录，对应 eb_assess_user_score 表。
 *
 * @author qinlei
 * @date 2026/4/5 18:30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "绩效考核评分记录")
@TableName("eb_assess_user_score")
public class AssessUserScore extends Req<AssessUserScore> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "企业ID")
	private Long entid;

	@Schema(description = "考核记录ID")
	private Integer assessid;

	@Schema(description = "操作人ID")
	@TableField("userid")
	private Integer userid;

	@Schema(description = "考核人ID")
	@TableField("check_uid")
	private Integer checkUid;

	@Schema(description = "被考核人ID")
	@TableField("test_uid")
	private Integer testUid;

	@Schema(description = "考核得分")
	private BigDecimal score;

	@Schema(description = "最高分")
	private BigDecimal total;

	@Schema(description = "考核等级")
	private Integer grade;

	@Schema(description = "变更说明")
	private String info;

	@Schema(description = "备注")
	private String mark;

	@Schema(description = "0评分 1删除绩效")
	private Integer types;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

}
