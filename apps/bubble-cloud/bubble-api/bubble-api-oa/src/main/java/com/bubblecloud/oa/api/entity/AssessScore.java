package com.bubblecloud.oa.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bubblecloud.common.mybatis.base.Req;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 绩效考核评分级别，对应 eb_assess_score 表。
 *
 * @author qinlei
 * @date 2026/3/31 14:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "绩效考核评分级别")
@TableName("eb_assess_score")
public class AssessScore extends Req<AssessScore> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "企业ID")
	private Long entid;

	@Schema(description = "用户关联企业表(admin主键)ID")
	private Integer userId;

	@Schema(description = "等级名称")
	private String name;

	@Schema(description = "分数最小值")
	private Integer min;

	@Schema(description = "分数最大值")
	private Integer max;

	@Schema(description = "级别")
	private Integer level;

	@Schema(description = "说明")
	private String mark;

}
