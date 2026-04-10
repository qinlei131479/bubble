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
 * 客户关注，对应 eb_client_subscribe 表。
 *
 * @author qinlei
 * @date 2026/4/3 10:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "客户关注")
@TableName("eb_client_subscribe")
public class ClientSubscribe extends Req<ClientSubscribe> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "企业ID")
	private Integer entid;

	@Schema(description = "用户ID")
	private Integer uid;

	@Schema(description = "客户ID")
	private Integer eid;

	@Schema(description = "关注状态")
	private Integer subscribeStatus;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

}
