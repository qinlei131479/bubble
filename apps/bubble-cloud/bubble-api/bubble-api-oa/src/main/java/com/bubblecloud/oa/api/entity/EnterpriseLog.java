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
 * 企业操作日志，对应 eb_enterprise_log_1 表（分表结构相同，可按企业路由切换物理表名）。
 *
 * @author qinlei
 * @date 2026/3/30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "企业操作日志")
@TableName("eb_enterprise_log_1")
public class EnterpriseLog extends Req<EnterpriseLog> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "用户 UID 字符串")
	private String uid;

	@Schema(description = "操作人姓名")
	private String userName;

	@Schema(description = "请求路径")
	private String path;

	@Schema(description = "HTTP 方法")
	private String method;

	@Schema(description = "行为描述")
	private String eventName;

	@Schema(description = "企业ID")
	private Integer entid;

	@Schema(description = "类型")
	private String type;

	@Schema(description = "终端")
	private String terminal;

	@Schema(description = "IP")
	private String lastIp;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

}
