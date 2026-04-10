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
 * 项目/任务动态，对应 eb_program_dynamic 表。
 *
 * @author qinlei
 * @date 2026/4/8 10:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "项目动态")
@TableName("eb_program_dynamic")
public class ProgramDynamic extends Req<ProgramDynamic> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "动态类型 1项目 2任务")
	private Integer types;

	@Schema(description = "操作人ID")
	private Long uid;

	@Schema(description = "操作人姓名")
	private String operator;

	@Schema(description = "关联业务ID（项目ID或任务ID）")
	private Long relationId;

	@Schema(description = "动作类型 1创建 2修改 3删除")
	private Integer actionType;

	@Schema(description = "标题说明")
	private String title;

	@Schema(description = "描述（JSON 文本）")
	private String describe;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

}
