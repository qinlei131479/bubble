package com.bubblecloud.oa.api.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bubblecloud.common.mybatis.base.Req;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 客户发票，对应 eb_client_invoice 表。
 *
 * @author qinlei
 * @date 2026/4/3 10:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "客户发票")
@TableName("eb_client_invoice")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientInvoice extends Req<ClientInvoice> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "企业ID")
	private Integer entid;

	@TableField("`unique`")
	@Schema(description = "唯一值")
	private String invoiceUnique;

	@Schema(description = "发票流水号")
	private String serialNumber;

	@Schema(description = "业务员UUID")
	private String uid;

	@Schema(description = "客户ID")
	private Integer eid;

	@Schema(description = "合同ID")
	private Integer cid;

	@Schema(description = "发票类目ID")
	private Integer categoryId;

	@Schema(description = "发票名称")
	private String name;

	@Schema(description = "发票编号")
	private String num;

	@Schema(description = "合同金额")
	private BigDecimal price;

	@Schema(description = "发票金额")
	private BigDecimal amount;

	@Schema(description = "发票类型")
	private String types;

	@Schema(description = "发票抬头")
	private String title;

	@Schema(description = "纳税人识别号")
	private String ident;

	@Schema(description = "开户行")
	private String bank;

	@Schema(description = "开户账号")
	private String account;

	@Schema(description = "开票地址")
	private String address;

	@Schema(description = "电话")
	private String tel;

	@Schema(description = "邮寄联系人")
	private String collectName;

	@Schema(description = "邮寄联系电话")
	private String collectTel;

	@Schema(description = "邮寄方式")
	private String collectType;

	@Schema(description = "邮寄邮箱")
	private String collectEmail;

	@Schema(description = "邮寄地址")
	private String mailAddress;

	@Schema(description = "开票方式")
	private String invoiceType;

	@Schema(description = "开票地址")
	private String invoiceAddress;

	@Schema(description = "发票状态")
	private Integer status;

	@Schema(description = "作废状态")
	private Integer invalid;

	@Schema(description = "开票日期")
	private LocalDate billDate;

	@Schema(description = "实际开票日期")
	private LocalDate realDate;

	@Schema(description = "备注")
	private String mark;

	@Schema(description = "开票备注")
	private String remark;

	@Schema(description = "业务员备注")
	private String cardRemark;

	@Schema(description = "财务备注")
	private String financeRemark;

	@Schema(description = "创建人UUID")
	private String creator;

	@Schema(description = "关联审批ID")
	private Integer linkId;

	@Schema(description = "撤销申请ID")
	private Integer revokeId;

	@Schema(description = "关联付款单ID")
	private String linkBill;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

}
