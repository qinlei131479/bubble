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
 * 系统消息，对应 eb_message 表。
 *
 * @author qinlei
 * @date 2026/3/29 18:30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "系统消息")
@TableName("eb_message")
public class Message extends Req<Message> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "企业ID")
	private Long entid;

	@Schema(description = "关联业务ID")
	@TableField("relation_id")
	private Long relationId;

	@Schema(description = "分类ID")
	@TableField("cate_id")
	private Long cateId;

	@Schema(description = "关联 curl ID")
	@TableField("curl_id")
	private Integer curlId;

	@Schema(description = "分类名称")
	@TableField("cate_name")
	private String cateName;

	@Schema(description = "模板类型")
	@TableField("template_type")
	private String templateType;

	@Schema(description = "模板变量 JSON")
	@TableField("template_var")
	private String templateVar;

	@Schema(description = "模板时间")
	@TableField("template_time")
	private Integer templateTime;

	@Schema(description = "标题")
	private String title;

	@Schema(description = "内容")
	private String content;

	@Schema(description = "提醒时间")
	@TableField("remind_time")
	private String remindTime;

	@Schema(description = "低代码表单ID")
	@TableField("crud_id")
	private Integer crudId;

	@Schema(description = "事件ID")
	@TableField("event_id")
	private Integer eventId;

	@Schema(description = "用户订阅标识")
	@TableField("user_sub")
	private Integer userSub;

	@TableLogic
	@TableField("deleted_at")
	@Schema(description = "删除时间")
	private LocalDateTime deletedAt;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

}
