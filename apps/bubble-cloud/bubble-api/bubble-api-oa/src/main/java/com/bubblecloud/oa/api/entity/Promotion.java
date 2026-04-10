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
 * 晋升配置，对应 eb_promotion 表。
 *
 * @author qinlei
 * @date 2026/3/29 下午6:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "晋升表")
@TableName("eb_promotion")
public class Promotion extends Req<Promotion> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "晋升名称")
	private String name;

	@Schema(description = "排序")
	private Integer sort;

	@Schema(description = "1 展示 0 关闭")
	private Integer status;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

	@Schema(description = "删除时间")
	@TableLogic
	private LocalDateTime deletedAt;

}
