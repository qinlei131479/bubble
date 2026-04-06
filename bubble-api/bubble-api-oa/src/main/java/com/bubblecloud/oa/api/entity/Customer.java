package com.bubblecloud.oa.api.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bubblecloud.common.mybatis.base.Req;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 客户主表，对应 eb_customer 表。
 *
 * @author qinlei
 * @date 2026/4/3 10:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "客户")
@TableName("eb_customer")
public class Customer extends Req<Customer> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "业务员ID")
	private Integer uid;

	@Schema(description = "前业务员ID")
	private Integer beforeUid;

	@Schema(description = "创建人ID")
	private Integer creatorUid;

	@Schema(description = "客户名称")
	private String customerName;

	@Schema(description = "客户标签")
	private String customerLabel;

	@Schema(description = "客户编号")
	private String customerNo;

	@Schema(description = "客户来源")
	private String customerWay;

	@Schema(description = "未跟进天数")
	private Integer unFollowedDays;

	@Schema(description = "已入账金额")
	private BigDecimal amountRecorded;

	@Schema(description = "已支出金额")
	private BigDecimal amountExpend;

	@Schema(description = "已开票金额")
	private BigDecimal invoicedAmount;

	@Schema(description = "合同数量")
	private Integer contractNum;

	@Schema(description = "发票数量")
	private Integer invoiceNum;

	@Schema(description = "附件数量")
	private Integer attachmentNum;

	@Schema(description = "退回次数")
	private Integer returnNum;

	@Schema(description = "是否关注")
	private String customerFollowed;

	@Schema(description = "客户状态")
	private String customerStatus;

	@Schema(description = "省市区")
	private String areaCascade;

	@Schema(description = "备注")
	private String b37a3f36;

	@Schema(description = "企业电话")
	private String b37a3f16;

	@TableField("9bfe77e4")
	@Schema(description = "详细地址")
	private String col9bfe77e4;

	@TableField("7763f80f")
	@Schema(description = "客户附件")
	private String col7763f80f;

	@Schema(description = "最后跟进时间")
	private LocalDateTime lastFollowUpTime;

	@Schema(description = "领取时间")
	private LocalDateTime collectTime;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

	@Schema(description = "删除时间")
	private LocalDateTime deletedAt;

	@Schema(description = "备注")
	private String c839a357;

	@Schema(description = "附件")
	private String c254fbdb;

	@TableField(exist = false)
	@Schema(description = "列表查询：业务类型 1全部视角 2我的 3公海")
	private Integer queryTypes;

	@TableField(exist = false)
	@Schema(description = "列表查询：业务员 uid 集合（types=1 时来自请求；types=2 时为当前用户）")
	private List<Integer> queryUidList;

	@TableField(exist = false)
	@Schema(description = "列表查询：按标签 ID 筛选（与 eb_client_labels 关联，任一匹配即命中）")
	private List<Integer> queryLabelIds;

}
