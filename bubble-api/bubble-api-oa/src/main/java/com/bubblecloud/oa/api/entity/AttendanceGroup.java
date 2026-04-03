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
 * 考勤组，对应 eb_attendance_group 表。
 *
 * @author qinlei
 * @date 2026/4/2 16:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "考勤组")
@TableName("eb_attendance_group")
public class AttendanceGroup extends Req<AttendanceGroup> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Integer id;

	@Schema(description = "考勤组名称")
	private String name;

	@Schema(description = "考勤类型：0 人员；1 部门")
	private Integer type;

	@Schema(description = "详细地址")
	private String address;

	@Schema(description = "纬度")
	private String lat;

	@Schema(description = "经度")
	private String lng;

	@Schema(description = "有效范围")
	@TableField("effective_range")
	private Integer effectiveRange;

	@Schema(description = "考勤地点名称")
	@TableField("location_name")
	private String locationName;

	@Schema(description = "允许补卡")
	@TableField("repair_allowed")
	private Integer repairAllowed;

	@Schema(description = "补卡类型 JSON 数组字符串")
	@TableField("repair_type")
	private String repairType;

	@Schema(description = "补卡时间限制")
	@TableField("is_limit_time")
	private Integer isLimitTime;

	@Schema(description = "补卡时间")
	@TableField("limit_time")
	private Integer limitTime;

	@Schema(description = "补卡次数限制")
	@TableField("is_limit_number")
	private Integer isLimitNumber;

	@Schema(description = "补卡次数")
	@TableField("limit_number")
	private Integer limitNumber;

	@Schema(description = "拍照打卡")
	@TableField("is_photo")
	private Integer isPhoto;

	@Schema(description = "外勤打卡")
	@TableField("is_external")
	private Integer isExternal;

	@Schema(description = "外勤备注")
	@TableField("is_external_note")
	private Integer isExternalNote;

	@Schema(description = "外勤拍照")
	@TableField("is_external_photo")
	private Integer isExternalPhoto;

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
	@Schema(description = "列表查询：名称模糊")
	private String nameLike;

}
