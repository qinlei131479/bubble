package com.bubblecloud.oa.api.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bubblecloud.common.mybatis.base.Req;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 项目版本，对应 eb_program_version 表。
 *
 * @author qinlei
 * @date 2026/4/8 10:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "项目版本")
@TableName("eb_program_version")
public class ProgramVersion extends Req<ProgramVersion> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "项目ID")
	private Long programId;

	@Schema(description = "版本名称")
	private String name;

	@Schema(description = "创建人ID")
	private Long creatorUid;

	@Schema(description = "排序")
	private Integer sort;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

	@Schema(description = "删除时间")
	@TableLogic
	private LocalDateTime deletedAt;

}
