package com.bubblecloud.oa.api.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bubblecloud.common.mybatis.base.Req;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 审批申请表，对应 eb_approve_apply 表。
 *
 * @author qinlei
 * @date 2026/4/6 12:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "审批申请")
@TableName("eb_approve_apply")
public class ApproveApply extends Req<ApproveApply> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "申请人 admin.id")
	@TableField("user_id")
	private Long userId;

	@Schema(description = "企业ID")
	private Long entid;

	@Schema(description = "名片ID")
	@TableField("card_id")
	private Long cardId;

	@Schema(description = "审批配置ID")
	@TableField("approve_id")
	private Long approveId;

	@Schema(description = "当前节点")
	@TableField("node_id")
	private String nodeId;

	@Schema(description = "是否需要审批")
	private Integer examine;

	@Schema(description = "状态")
	private Integer status;

	@Schema(description = "说明")
	private String info;

	@Schema(description = "编号")
	private String number;

	@Schema(description = "低代码实体ID")
	@TableField("crud_id")
	private Long crudId;

	@Schema(description = "业务数据ID")
	@TableField("link_id")
	private Long linkId;

	@Schema(description = "关联申请")
	@TableField("apply_id")
	private Long applyId;

	@Schema(description = "是否撤销审批")
	@TableField("is_recall")
	private Integer isRecall;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

	@TableLogic
	@Schema(description = "删除时间")
	private LocalDateTime deletedAt;

	@TableField(exist = false)
	@Schema(description = "列表筛选：企业ID")
	private Long filterEntid;

}
