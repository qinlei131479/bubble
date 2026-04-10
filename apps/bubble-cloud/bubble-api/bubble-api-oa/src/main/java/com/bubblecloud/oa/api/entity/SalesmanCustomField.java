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
 * 业务员自定义列表字段，对应 eb_salesman_custom_field 表。
 *
 * @author qinlei
 * @date 2026/3/30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "业务员自定义字段配置")
@TableName("eb_salesman_custom_field")
public class SalesmanCustomField extends Req<SalesmanCustomField> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "用户ID（admin 主键）")
	private Integer uid;

	@Schema(description = "业务类型键，如 1_list、1_search")
	@TableField("custom_type")
	private String customType;

	@Schema(description = "自定义数据 JSON")
	@TableField("field_list")
	private String fieldList;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

}
