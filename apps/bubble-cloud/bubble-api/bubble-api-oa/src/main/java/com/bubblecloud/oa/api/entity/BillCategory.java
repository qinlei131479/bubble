package com.bubblecloud.oa.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bubblecloud.common.mybatis.base.Req;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 财务流水分类，对应 eb_bill_category 表。
 *
 * @author qinlei
 * @date 2026/4/6 15:30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "财务流水分类")
@TableName("eb_bill_category")
public class BillCategory extends Req<BillCategory> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "企业ID")
	private Long entid;

	@Schema(description = "路径（PHP PathAttrTrait：/1/2/）")
	private String path;

	@Schema(description = "级别")
	private Integer level;

	@Schema(description = "分类名称")
	private String name;

	@Schema(description = "分类编号")
	private String cateNo;

	@Schema(description = "排序")
	private Integer sort;

	@Schema(description = "上级ID")
	private Integer pid;

	@Schema(description = "0支出 1收入")
	private Integer types;

	@TableField("`contact id`")
	@Schema(description = "联系人ID")
	private Integer contactId;

}
