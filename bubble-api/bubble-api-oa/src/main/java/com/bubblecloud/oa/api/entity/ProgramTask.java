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
 * 项目任务（统计用最小字段），对应 eb_program_task 表。
 *
 * @author qinlei
 * @date 2026/4/6 16:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "项目任务")
@TableName("eb_program_task")
public class ProgramTask extends Req<ProgramTask> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "项目ID")
	private Long programId;

	@Schema(description = "状态")
	private Integer status;

	@Schema(description = "删除时间")
	@TableLogic
	private LocalDateTime deletedAt;

}
