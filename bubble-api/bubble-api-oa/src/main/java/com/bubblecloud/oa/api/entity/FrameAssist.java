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
 * 组织架构人员关联，对应 eb_frame_assist 表。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "组织架构人员关联")
@TableName("eb_frame_assist")
public class FrameAssist extends Req<FrameAssist> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "企业ID")
	private Long entid;

	@Schema(description = "部门ID（enterprise_frame）")
	private Integer frameId;

	@Schema(description = "员工ID（eb_admin.id）")
	private Long userId;

	@Schema(description = "是否主部门：1=是；0=否")
	private Integer isMastart;

	@Schema(description = "是否部门主管：1=是；0=否")
	private Integer isAdmin;

	@Schema(description = "上级主管用户ID")
	private Long superiorUid;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "删除时间")
	private LocalDateTime deletedAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

}
