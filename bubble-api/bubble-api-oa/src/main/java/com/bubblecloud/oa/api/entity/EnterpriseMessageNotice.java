package com.bubblecloud.oa.api.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bubblecloud.common.mybatis.base.Req;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 企业消息通知记录，对应 eb_enterprise_message_notice 表。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "企业消息通知")
@TableName("eb_enterprise_message_notice")
public class EnterpriseMessageNotice extends Req<EnterpriseMessageNotice> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "企业ID")
	private Long entid;

	@Schema(description = "发送人或者企业ID")
	private Integer sendId;

	@Schema(description = "接收方用户标识（可为 admin.id 或 uid 字符串）")
	private String toUid;

	@Schema(description = "跳转链接")
	private String url;

	@Schema(description = "uni 跳转路径")
	private String uniUrl;

	@Schema(description = "图片")
	private String image;

	@Schema(description = "消息标题")
	private String title;

	@Schema(description = "消息内容")
	private String message;

	@Schema(description = "消息类型：1=系统消息；0=个人消息；3=企业站内消息")
	private Integer type;

	@Schema(description = "消息分类ID")
	private Integer cateId;

	@Schema(description = "消息模板ID")
	private Integer messageId;

	@Schema(description = "分类名称")
	private String cateName;

	@Schema(description = "是否已读：1=已读；0=未读")
	private Integer isRead;

	@Schema(description = "是否已处理")
	private Integer isHandle;

	@Schema(description = "是否显示")
	private Integer isShow;

	@Schema(description = "模板类型")
	private String templateType;

	@Schema(description = "按钮模板（JSON）")
	private String buttonTemplate;

	@Schema(description = "其他附加内容（JSON）")
	private String other;

	@Schema(description = "关联记录ID")
	private Integer linkId;

	@Schema(description = "关联记录状态")
	private Integer linkStatus;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

}
