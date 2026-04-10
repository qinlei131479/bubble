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
 * 合同附件备注，对应 eb_contract_resource 表。
 *
 * @author qinlei
 * @date 2026/4/3 10:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "合同附件")
@TableName("eb_contract_resource")
public class ContractResource extends Req<ContractResource> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "企业ID")
	private Integer entid;

	@Schema(description = "客户ID")
	private Integer eid;

	@Schema(description = "合同ID")
	private Integer cid;

	@Schema(description = "用户ID")
	private Integer uid;

	@Schema(description = "备注内容")
	private String content;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

}
