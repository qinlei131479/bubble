package com.bubblecloud.oa.api.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bubblecloud.common.mybatis.base.Req;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 企业员工人事变动，对应 eb_enterprise_user_change。
 *
 * @author qinlei
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "企业员工人事变动")
@TableName("eb_enterprise_user_change")
public class EnterpriseUserChange extends Req<EnterpriseUserChange> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	private Long id;

	private Long uid;

	private Long entid;

	@Schema(description = "名片/员工 admin.id")
	private Long cardId;

	private Integer types;

	private LocalDate date;

	private Integer newFrame;

	private Integer oldFrame;

	private Integer newPosition;

	private Integer oldPosition;

	private String info;

	private String mark;

	private Integer linkId;

	private Integer userId;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

}
