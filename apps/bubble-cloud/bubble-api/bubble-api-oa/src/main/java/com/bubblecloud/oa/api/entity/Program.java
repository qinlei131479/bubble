package com.bubblecloud.oa.api.entity;

import java.time.LocalDate;
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
 * 项目，对应 eb_program 表。
 *
 * @author qinlei
 * @date 2026/4/6 15:30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "项目")
@TableName("eb_program")
public class Program extends Req<Program> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "名称")
	private String name;

	@Schema(description = "编号")
	private String ident;

	@Schema(description = "负责人ID")
	private Long uid;

	@Schema(description = "客户ID")
	private Long eid;

	@Schema(description = "合同ID")
	private Long cid;

	@Schema(description = "创建人ID")
	private Long creatorUid;

	@Schema(description = "开始日期")
	private LocalDate startDate;

	@Schema(description = "结束日期")
	private LocalDate endDate;

	@Schema(description = "状态 0正常 1暂停 2关闭")
	private Integer status;

	@Schema(description = "描述")
	private String describe;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

	@Schema(description = "删除时间")
	@TableLogic
	private LocalDateTime deletedAt;

}
