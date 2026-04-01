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
 * 海氏评估数据，对应 eb_hay_group_data 表。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "海氏评估数据")
@TableName("eb_hay_group_data")
public class HayGroupData extends Req<HayGroupData> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "评估组ID")
	private Long groupId;

	@Schema(description = "企业ID")
	private Long entid;

	@Schema(description = "用户ID(admin)")
	private Long userId;

	@Schema(description = "知识技能得分")
	private Integer knowScore;

	@Schema(description = "解决问题得分")
	private Integer problemScore;

	@Schema(description = "承担责任得分")
	private Integer dutyScore;

	@Schema(description = "总分")
	private Integer totalScore;

	@Schema(description = "评估时间")
	private LocalDateTime assessTime;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

}
