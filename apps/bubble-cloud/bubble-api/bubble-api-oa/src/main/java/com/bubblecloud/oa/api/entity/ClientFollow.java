package com.bubblecloud.oa.api.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bubblecloud.common.mybatis.base.Req;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 客户跟进记录，对应 eb_client_follow 表。
 *
 * @author qinlei
 * @date 2026/4/3 10:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "客户跟进")
@TableName("eb_client_follow")
public class ClientFollow extends Req<ClientFollow> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "客户ID")
	private Integer eid;

	@Schema(description = "用户ID")
	private Integer userId;

	@Schema(description = "说明内容")
	private String content;

	@Schema(description = "类型：0说明；1提醒")
	private Integer types;

	@Schema(description = "提醒时间")
	private LocalDateTime time;

	@Schema(description = "定时任务唯一值")
	private String uniqued;

	@Schema(description = "状态")
	private Integer status;

	@Schema(description = "删除时间")
	private LocalDateTime deletedAt;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

	@Schema(description = "跟进版本")
	private Integer followVersion;

	/** 完成关联的跟进提醒 ID（请求体，非表字段） */
	@TableField(exist = false)
	@Schema(description = "关联跟进提醒ID")
	private Long followId;

	/** 附件 ID 列表（请求体，对齐 PHP {@code attach_ids}） */
	@TableField(exist = false)
	@JsonProperty("attach_ids")
	@Schema(description = "附件ID列表")
	private List<Integer> attachIds;

	/** 列表接口填充，对齐 PHP with attachs */
	@TableField(exist = false)
	@Schema(description = "关联附件列表")
	private List<SystemAttach> attachs;

}
