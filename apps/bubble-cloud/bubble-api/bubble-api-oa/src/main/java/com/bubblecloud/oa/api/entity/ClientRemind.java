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
 * 付款/续费提醒，对应 eb_client_remind 表。
 *
 * @author qinlei
 * @date 2026/4/3 10:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "付款提醒")
@TableName("eb_client_remind")
public class ClientRemind extends Req<ClientRemind> {

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

	@Schema(description = "用户ID")
	private Integer userId;

	@Schema(description = "付款单ID")
	private Integer billId;

	@Schema(description = "金额")
	private BigDecimal num;

	@Schema(description = "备注")
	private String mark;

	@Schema(description = "提醒时间")
	private LocalDateTime time;

	@Schema(description = "本期时间")
	private LocalDateTime thisPeriod;

	@Schema(description = "下期时间")
	private LocalDateTime nextPeriod;

	@Schema(description = "定时任务唯一值")
	private String uniqued;

	@Schema(description = "重复频率")
	private Integer rate;

	@Schema(description = "重复周期")
	private Integer period;

	@Schema(description = "类型：0回款；1续费")
	private Integer types;

	@Schema(description = "状态")
	private Integer status;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

	@Schema(description = "删除时间")
	private LocalDateTime deletedAt;

}
