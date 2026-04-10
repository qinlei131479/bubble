package com.bubblecloud.oa.api.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bubblecloud.common.mybatis.base.Req;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 自定义表单分组，对应 eb_form_cate 表。
 *
 * @author qinlei
 * @date 2026/3/30 下午11:45
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "表单分组")
@TableName("eb_form_cate")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class FormCategory extends Req<FormCategory> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "分组名称")
	private String title;

	@Schema(description = "排序")
	private Integer sort;

	@Schema(description = "分组类型：1 客户 2 合同 3 联系人")
	private Integer types;

	@Schema(description = "状态：1 显示 0 隐藏")
	private Integer status;

	@TableLogic
	@TableField("deleted_at")
	@Schema(description = "删除时间")
	private LocalDateTime deletedAt;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

}
