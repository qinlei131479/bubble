package com.bubblecloud.oa.api.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bubblecloud.common.mybatis.base.Req;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统备份记录，对应 eb_system_backup 表。
 *
 * @author qinlei
 * @date 2026/4/4 16:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "系统备份")
@TableName("eb_system_backup")
public class SystemBackup extends Req<SystemBackup> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键")
	private Long id;

	@Schema(description = "企业ID")
	private Integer entid;

	@Schema(description = "文件路径")
	private String path;

	@Schema(description = "创建用户UID")
	private String uid;

	@Schema(description = "版本号")
	private String version;

	@TableField("created_at")
	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@TableField("updated_at")
	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

}
