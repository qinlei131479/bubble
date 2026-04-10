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
 * 企业支付方式，对应 eb_enterprise_paytype 表。
 *
 * @author qinlei
 * @date 2026/4/6 15:30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "企业支付方式")
@TableName("eb_enterprise_paytype")
public class EnterprisePaytype extends Req<EnterprisePaytype> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "企业ID")
	private Integer entid;

	@Schema(description = "系统支付方式ID")
	private Integer typeId;

	@Schema(description = "名称")
	private String name;

	@Schema(description = "标识")
	private String ident;

	@Schema(description = "简介")
	private String info;

	@Schema(description = "1可用 0不可用")
	private Integer status;

	@Schema(description = "排序")
	private Integer sort;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

}
