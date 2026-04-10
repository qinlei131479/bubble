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
 * 企业员工人事变动，对应 eb_enterprise_user_change 表。
 *
 * @author qinlei
 * @date 2026/4/5
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "企业员工人事变动")
@TableName("eb_enterprise_user_change")
public class EnterpriseUserChange extends Req<EnterpriseUserChange> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "用户UID")
	private Long uid;

	@Schema(description = "企业ID")
	private Long entid;

	@Schema(description = "名片/员工 admin.id")
	private Long cardId;

	@Schema(description = "变动类型")
	private Integer types;

	@Schema(description = "生效日期")
	private LocalDate date;

	@Schema(description = "新部门/架构ID")
	private Integer newFrame;

	@Schema(description = "原部门/架构ID")
	private Integer oldFrame;

	@Schema(description = "新岗位ID")
	private Integer newPosition;

	@Schema(description = "原岗位ID")
	private Integer oldPosition;

	@Schema(description = "变动说明")
	private String info;

	@Schema(description = "备注")
	private String mark;

	@Schema(description = "关联业务ID")
	private Integer linkId;

	@Schema(description = "操作人用户ID")
	private Integer userId;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

}
