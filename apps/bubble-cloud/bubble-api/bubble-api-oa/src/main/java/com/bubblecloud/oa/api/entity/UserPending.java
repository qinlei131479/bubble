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
 * 用户待办，对应 eb_user_pending 表。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "用户待办")
@TableName("eb_user_pending")
public class UserPending extends Req<UserPending> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "用户UID")
	private String uid;

	@Schema(description = "企业ID")
	private Long entid;

	@Schema(description = "待办类型：1=绩效；2=日报")
	private Integer type;

	@Schema(description = "跳转路径")
	private String url;

	@Schema(description = "待办内容")
	private String content;

	@Schema(description = "待办事件结束时间")
	private LocalDateTime pendEntTime;

	@Schema(description = "状态：1=已处理；0=未处理")
	private Integer status;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

}
