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
 * 省市区数据，对应 eb_system_city 表。
 *
 * @author qinlei
 * @date 2026/4/4 16:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "省市区")
@TableName("eb_system_city")
public class SystemCity extends Req<SystemCity> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键")
	private Long id;

	@TableField("city_id")
	@Schema(description = "城市编码")
	private Integer cityId;

	@Schema(description = "级别")
	private Integer level;

	@TableField("parent_id")
	@Schema(description = "父级 city_id")
	private Integer parentId;

	@TableField("area_code")
	@Schema(description = "区号")
	private String areaCode;

	@Schema(description = "名称")
	private String name;

	@TableField("merger_name")
	@Schema(description = "合并名称")
	private String mergerName;

	@Schema(description = "经度")
	private String lng;

	@Schema(description = "纬度")
	private String lat;

	@TableField("is_show")
	@Schema(description = "是否展示")
	private Integer isShow;

}
