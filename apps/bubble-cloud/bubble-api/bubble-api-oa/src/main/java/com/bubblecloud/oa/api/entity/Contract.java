package com.bubblecloud.oa.api.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
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
 * 客户合同，对应 eb_contract 表。
 *
 * @author qinlei
 * @date 2026/4/3 10:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "客户合同")
@TableName("eb_contract")
public class Contract extends Req<Contract> {

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

	@Schema(description = "合同名称")
	private String contractName;

	@Schema(description = "合同编号")
	private String contractNo;

	@Schema(description = "合同金额")
	private BigDecimal contractPrice;

	@Schema(description = "回款金额")
	private BigDecimal received;

	@Schema(description = "尾款金额")
	private BigDecimal surplus;

	@Schema(description = "是否关注")
	private String contractFollowed;

	@Schema(description = "合同状态")
	private String contractStatus;

	@Schema(description = "是否有续费")
	private Integer renew;

	@Schema(description = "开始时间")
	private LocalDate startDate;

	@Schema(description = "结束时间")
	private LocalDate endDate;

	@Schema(description = "签约状态")
	private String signingStatus;

	@Schema(description = "备注")
	private String b3733f36;

	@Schema(description = "合同分类")
	private String contractCategory;

	@Schema(description = "合同分类副本")
	private String contractCate;

	@Schema(description = "是否异常")
	private Integer isAbnormal;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

	@Schema(description = "删除时间")
	private LocalDateTime deletedAt;

	@TableField("c111b844")
	@Schema(description = "附件")
	private String c111b844;

	@TableField("cfe7a0d6")
	@Schema(description = "附件")
	private String cfe7a0d6;

	@TableField("c8a7ea50")
	@Schema(description = "合同附件")
	private String c8a7ea50;

	@TableField("ca1e5ae5")
	@Schema(description = "图片")
	private String ca1e5ae5;

	@TableField(exist = false)
	@Schema(description = "列表：5全部 6我的")
	private Integer queryTypes;

	@TableField(exist = false)
	@Schema(description = "列表：业务员 uid 集合")
	private List<Integer> queryUidList;

}
