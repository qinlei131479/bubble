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
 * 系统消息配置，对应 eb_message（避免与 java.lang 包名冲突命名为 OaMessage）。
 *
 * @author qinlei
 * @date 2026/3/29 20:30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "系统消息")
@TableName("eb_message")
public class OaMessage extends Req<OaMessage> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	private Long id;

	private Integer entid;

	@TableField("relation_id")
	private Integer relationId;

	@TableField("cate_id")
	private Integer cateId;

	@TableField("curl_id")
	private Integer curlId;

	@TableField("cate_name")
	private String cateName;

	@TableField("template_type")
	private String templateType;

	@TableField("template_var")
	private String templateVar;

	@TableField("template_time")
	private Integer templateTime;

	private String title;

	private String content;

	@TableField("remind_time")
	private String remindTime;

	@TableField("crud_id")
	private Integer crudId;

	@TableField("event_id")
	private Integer eventId;

	@TableField("user_sub")
	private Integer userSub;

	@TableLogic
	@TableField("deleted_at")
	private LocalDateTime deletedAt;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

}
