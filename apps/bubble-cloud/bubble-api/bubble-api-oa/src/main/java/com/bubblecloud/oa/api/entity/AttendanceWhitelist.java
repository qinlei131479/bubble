package com.bubblecloud.oa.api.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bubblecloud.common.mybatis.base.Req;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 考勤白名单，对应 eb_attendance_whitelist 表。
 *
 * @author qinlei
 * @date 2026/4/2 16:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "考勤白名单")
@TableName("eb_attendance_whitelist")
public class AttendanceWhitelist extends Req<AttendanceWhitelist> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Integer id;

	@Schema(description = "业务员 admin.id")
	private Integer uid;

	@Schema(description = "0 人员；1 管理员")
	private Integer type;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

	@TableLogic
	@Schema(description = "删除时间")
	private LocalDateTime deletedAt;

}
