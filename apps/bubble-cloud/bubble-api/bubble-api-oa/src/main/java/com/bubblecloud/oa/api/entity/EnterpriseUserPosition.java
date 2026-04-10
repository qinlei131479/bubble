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
 * 企业员工任职经历，对应 eb_enterprise_user_position 表。
 *
 * @author qinlei
 * @date 2026/4/5
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "企业员工任职经历")
@TableName("eb_enterprise_user_position")
public class EnterpriseUserPosition extends Req<EnterpriseUserPosition> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "名片/员工 admin.id")
	private Long cardId;

	@Schema(description = "任职开始时间")
	private LocalDateTime startTime;

	@Schema(description = "任职结束时间")
	private LocalDateTime endTime;

	@Schema(description = "岗位名称")
	private String position;

	@Schema(description = "部门")
	private String department;

	@Schema(description = "是否负责人")
	private Integer isAdmin;

	@Schema(description = "状态")
	private Integer status;

	@Schema(description = "备注")
	private String remark;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

}
