package com.bubblecloud.oa.api.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bubblecloud.common.mybatis.base.Req;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 排班周期，对应 eb_roster_cycle 表。
 *
 * @author qinlei
 * @date 2026/4/7 12:15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "排班周期")
@TableName("eb_roster_cycle")
public class RosterCycle extends Req<RosterCycle> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Integer id;

	@Schema(description = "考勤组ID")
	@TableField("group_id")
	private Integer groupId;

	@Schema(description = "周期名称")
	private String name;

	@Schema(description = "周期天数")
	private Integer cycle;

	@Schema(description = "创建人 admin.id")
	private Integer uid;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

	@TableLogic
	@Schema(description = "删除时间")
	private LocalDateTime deletedAt;

	@TableField(exist = false)
	@Schema(description = "按考勤组查询周期列表")
	private Integer queryGroupId;

}
