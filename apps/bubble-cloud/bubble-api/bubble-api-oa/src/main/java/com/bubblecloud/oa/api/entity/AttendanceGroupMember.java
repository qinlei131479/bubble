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
 * 考勤组成员，对应 eb_attendance_group_member 表。
 *
 * @author qinlei
 * @date 2026/4/2 16:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "考勤组成员")
@TableName("eb_attendance_group_member")
public class AttendanceGroupMember extends Req<AttendanceGroupMember> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Integer id;

	@Schema(description = "考勤组ID")
	@TableField("group_id")
	private Integer groupId;

	@Schema(description = "人员/部门/过滤等 ID")
	private Integer member;

	@Schema(description = "0 考勤人员；1 无需考勤；2 负责人；3 部门")
	private Integer type;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

	@TableLogic
	@Schema(description = "删除时间")
	private LocalDateTime deletedAt;

}
