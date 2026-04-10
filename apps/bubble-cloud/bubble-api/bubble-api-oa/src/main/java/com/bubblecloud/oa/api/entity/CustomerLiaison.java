package com.bubblecloud.oa.api.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bubblecloud.common.mybatis.base.Req;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 客户联系人，对应 eb_customer_liaison 表。
 *
 * @author qinlei
 * @date 2026/4/3 10:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "客户联系人")
@TableName("eb_customer_liaison")
public class CustomerLiaison extends Req<CustomerLiaison> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "业务员ID")
	private Integer uid;

	@Schema(description = "客户ID")
	private Integer eid;

	@Schema(description = "创建人ID")
	private Integer creatorUid;

	@Schema(description = "联系人姓名")
	private String liaisonName;

	@Schema(description = "联系电话")
	private String liaisonTel;

	@Schema(description = "联系人职位")
	private String liaisonJob;

	@TableField("e06d7153")
	@Schema(description = "性别")
	private String e06d7153;

	@TableField("e06d7152")
	@Schema(description = "联系人邮箱")
	private String e06d7152;

	@TableField("e06d7159")
	@Schema(description = "联系人微信")
	private String e06d7159;

	@TableField("l753bf282")
	@Schema(description = "备注")
	private String l753bf282;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

	@Schema(description = "删除时间")
	private LocalDateTime deletedAt;

	@TableField("cdc4d06a")
	@Schema(description = "联系人QQ")
	private String cdc4d06a;

}
