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
 * 企业邀请加入申请，对应 eb_user_enterprise_apply 表。
 *
 * @author qinlei
 * @date 2026/3/29 下午5:30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "企业邀请加入申请")
@TableName("eb_user_enterprise_apply")
public class UserEnterpriseApply extends Req<UserEnterpriseApply> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "企业ID")
	private Integer entid;

	@Schema(description = "送达人 UID")
	private String uid;

	@Schema(description = "状态：-1 待处理 0 拒绝 1 同意")
	private Integer status;

	@Schema(description = "审核：0 待审 1 通过 -1 拒绝")
	private Integer verify;

	@Schema(description = "申请时间")
	private LocalDateTime createdAt;

}
