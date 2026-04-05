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
 * 企业员工任职经历，对应 eb_enterprise_user_position。
 * @author qinlei
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "企业员工任职经历")
@TableName("eb_enterprise_user_position")
public class EnterpriseUserPosition extends Req<EnterpriseUserPosition> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	private Long id;

	private Long cardId;

	private LocalDateTime startTime;

	private LocalDateTime endTime;

	private String position;

	private String department;

	private Integer isAdmin;

	private Integer status;

	private String remark;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

}
