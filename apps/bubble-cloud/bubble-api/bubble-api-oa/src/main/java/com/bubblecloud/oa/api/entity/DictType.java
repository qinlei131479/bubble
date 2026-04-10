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
 * 数据字典分类，对应 eb_dict_type 表。
 *
 * @author qinlei
 * @date 2026/3/29 下午12:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "字典类型")
@TableName("eb_dict_type")
public class DictType extends Req<DictType> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "字典名称")
	private String name;

	@Schema(description = "字典标识")
	private String ident;

	@Schema(description = "关联业务")
	private String linkType;

	@Schema(description = "最大层级")
	private Integer level;

	@Schema(description = "状态")
	private Integer status;

	@Schema(description = "是否默认")
	private Integer isDefault;

	@Schema(description = "备注")
	private String mark;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

}
