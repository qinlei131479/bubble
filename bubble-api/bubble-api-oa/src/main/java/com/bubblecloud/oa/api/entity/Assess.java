package com.bubblecloud.oa.api.entity;

import java.math.BigDecimal;
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
 * 绩效考核，对应 eb_assess 表。
 *
 * @author qinlei
 * @date 2026/4/7 10:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "绩效考核")
@TableName("eb_assess")
public class Assess extends Req<Assess> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "企业ID")
	private Long entid;

	@Schema(description = "考核名称")
	private String name;

	@Schema(description = "周期:1周2月3年4半年5季度")
	private Integer period;

	@Schema(description = "考核计划ID（DB planid）")
	@TableField("planid")
	private Long planId;

	@Schema(description = "组织架构ID")
	@TableField("frame_id")
	private Integer frameId;

	@Schema(description = "考核批次ID")
	private Integer number;

	@Schema(description = "考核人（上级）admin id")
	@TableField("check_uid")
	private Long checkUid;

	@Schema(description = "被考核人 admin id")
	@TableField("test_uid")
	private Long testUid;

	@Schema(description = "考核开始时间")
	@TableField("start_time")
	private LocalDateTime startTime;

	@Schema(description = "目标制定截止时间")
	@TableField("make_time")
	private LocalDateTime makeTime;

	@Schema(description = "目标制定状态：0未制定 1已启用 2草稿")
	@TableField("make_status")
	private Integer makeStatus;

	@Schema(description = "考核结束时间")
	@TableField("end_time")
	private LocalDateTime endTime;

	@Schema(description = "自评状态：0未评价 1已评价 2草稿")
	@TableField("test_status")
	private Integer testStatus;

	@Schema(description = "上级评价截止时间")
	@TableField("check_end")
	private LocalDateTime checkEnd;

	@Schema(description = "上级评价状态：0未评价 1已评价 2草稿")
	@TableField("check_status")
	private Integer checkStatus;

	@Schema(description = "审核截止时间")
	@TableField("verify_time")
	private LocalDateTime verifyTime;

	@Schema(description = "审核状态：0未审核 1已审核")
	@TableField("verify_status")
	private Integer verifyStatus;

	@Schema(description = "考核得分")
	@TableField("score")
	private BigDecimal assessScore;

	@Schema(description = "满分")
	@TableField("total")
	private BigDecimal assessTotal;

	@Schema(description = "考核等级 level")
	@TableField("grade")
	private Integer assessGrade;

	@Schema(description = "考核状态：0目标制定 1自评期 2上级评价 3审核期 4结束")
	private Integer status;

	@Schema(description = "评分方式：0加权 1加和")
	private Integer types;

	@Schema(description = "内容完整性 1完整 0不完整")
	private Integer intact;

	@Schema(description = "是否启用 0否 1是")
	@TableField("is_show")
	private Integer isShow;

	@Schema(description = "自评说明")
	@TableField("self_reply")
	private String selfReply;

	@Schema(description = "上级评价说明")
	private String reply;

	@Schema(description = "上级评价（仅上级可见）")
	@TableField("hide_reply")
	private String hideReply;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

	@Schema(description = "删除时间（DB 列名为 delete）")
	@TableLogic
	@TableField("`delete`")
	private LocalDateTime deletedAt;

}
