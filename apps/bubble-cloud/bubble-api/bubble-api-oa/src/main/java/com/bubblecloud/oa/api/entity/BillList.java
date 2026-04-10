package com.bubblecloud.oa.api.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bubblecloud.common.mybatis.base.Req;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 企业财务流水，对应 eb_bill_list 表。
 *
 * @author qinlei
 * @date 2026/4/6 15:30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "财务流水")
@TableName("eb_bill_list")
public class BillList extends Req<BillList> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "企业ID")
	private Long entid;

	@Schema(description = "创建成员数字ID")
	private Integer userId;

	@Schema(description = "创建成员UUID")
	private String uid;

	@Schema(description = "财务分类ID")
	private Integer cateId;

	@Schema(description = "变动金额")
	private BigDecimal num;

	@Schema(description = "变动时间")
	private LocalDateTime editTime;

	@Schema(description = "1收入 0支出")
	private Integer types;

	@Schema(description = "支付方式ID")
	private Integer typeId;

	@Schema(description = "支付方式名称")
	private String payType;

	@Schema(description = "备注")
	private String mark;

	@Schema(description = "关联业务ID")
	private Integer linkId;

	@Schema(description = "订单ID")
	private Integer orderId;

	@Schema(description = "关联类型")
	private String linkCate;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

}
