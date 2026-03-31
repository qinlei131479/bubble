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
 * 云存储配置，对应 eb_system_storage 表。
 *
 * @author qinlei
 * @date 2026/3/30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "云存储配置")
@TableName("eb_system_storage")
public class SystemStorage extends Req<SystemStorage> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Integer id;

	@Schema(description = "AccessKey")
	@TableField("access_key")
	private String accessKey;

	@Schema(description = "类型：1本地 2七牛 3OSS 4COS")
	private Integer type;

	@Schema(description = "空间名")
	private String name;

	@Schema(description = "地域")
	private String region;

	@Schema(description = "权限")
	private String acl;

	@Schema(description = "空间域名")
	private String domain;

	@Schema(description = "CDN 域名")
	private String cdn;

	@Schema(description = "CNAME")
	private String cname;

	@Schema(description = "是否 HTTPS")
	@TableField("is_ssl")
	private Integer isSsl;

	@Schema(description = "状态")
	private Integer status;

	@Schema(description = "是否删除")
	@TableField("is_delete")
	private Integer isDelete;

	@Schema(description = "添加时间戳")
	@TableField("add_time")
	private Integer addTime;

	@Schema(description = "更新时间戳")
	@TableField("update_time")
	private Integer updateTime;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

}
