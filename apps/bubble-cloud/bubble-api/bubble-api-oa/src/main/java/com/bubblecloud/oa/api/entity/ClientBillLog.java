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
 * 财务流水操作日志（PHP BillLog），对应 eb_client_bill_log 表。
 *
 * @author qinlei
 * @date 2026/4/6 15:30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "财务流水日志")
@TableName("eb_client_bill_log")
public class ClientBillLog extends Req<ClientBillLog> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "企业ID")
	private Integer entid;

	@Schema(description = "财务流水ID")
	private Integer billListId;

	@Schema(description = "操作人ID")
	private Integer uid;

	@Schema(description = "操作类型")
	private Integer type;

	@Schema(description = "日志内容（JSON 文本）")
	private String operation;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

}
