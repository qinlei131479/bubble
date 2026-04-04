package com.bubblecloud.oa.api.entity;

import java.math.BigDecimal;
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
 * 客户资金记录，对应 eb_client_bill 表。
 *
 * @author qinlei
 * @date 2026/4/3 10:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "客户付款/续费记录")
@TableName("eb_client_bill")
public class ClientBill extends Req<ClientBill> {

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

	@Schema(description = "续费类型ID")
	private Integer cateId;

	@Schema(description = "账目分类ID")
	private Integer billCateId;

	@Schema(description = "类型:0支出;1收入")
	private Integer billTypes;

	@Schema(description = "用户UUID")
	private String uid;

	@Schema(description = "发票ID")
	private Integer invoiceId;

	@Schema(description = "金额")
	private BigDecimal num;

	@Schema(description = "备注")
	private String mark;

	@Schema(description = "类型：0合同；1续费")
	private Integer types;

	@Schema(description = "支付方式ID")
	private Integer typeId;

	@Schema(description = "支付方式名称")
	private String payType;

	@Schema(description = "收款日期")
	private LocalDateTime date;

	@Schema(description = "续费结束日期")
	private LocalDateTime endDate;

	@Schema(description = "付款单号")
	private String billNo;

	@Schema(description = "关联审批ID")
	private Integer applyId;

	@Schema(description = "审核状态")
	private Integer status;

	@Schema(description = "失败原因")
	private String failMsg;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

	/** 待开票列表联表查询：关联发票状态（非表字段） */
	@TableField(exist = false)
	@Schema(description = "关联发票状态")
	private Integer invoiceJoinStatus;

}
