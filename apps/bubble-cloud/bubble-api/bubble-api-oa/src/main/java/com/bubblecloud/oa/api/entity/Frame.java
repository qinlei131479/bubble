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
 * 企业组织架构部门，对应 eb_frame 表。
 *
 * @author qinlei
 * @date 2026/3/29 下午12:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "组织架构部门")
@TableName("eb_frame")
public class Frame extends Req<Frame> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "部门主管用户ID")
	private Long userId;

	@Schema(description = "企业ID")
	private Long entid;

	@Schema(description = "父级部门ID")
	private Long pid;

	@Schema(description = "关联角色ID")
	private Long roleId;

	@Schema(description = "部门名称")
	private String name;

	@Schema(description = "层级路径")
	private String path;

	@Schema(description = "部门介绍")
	private String introduce;

	@Schema(description = "排序")
	private Integer sort;

	@Schema(description = "用户数量（含子部门）")
	private Integer userCount;

	@Schema(description = "本部门人数")
	private Integer userSingleCount;

	@Schema(description = "是否显示")
	private Integer isShow;

	@Schema(description = "层级")
	private Integer level;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

	@Schema(description = "删除时间")
	private LocalDateTime deletedAt;

}
