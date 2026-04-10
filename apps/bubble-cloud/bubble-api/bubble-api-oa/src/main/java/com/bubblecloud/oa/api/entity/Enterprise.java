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
 * 企业信息，对应 eb_enterprise 表。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "企业信息")
@TableName("eb_enterprise")
public class Enterprise extends Req<Enterprise> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "管理后台标题")
	private String title;

	@Schema(description = "企业名称")
	@TableField("enterprise_name")
	private String name;

	@Schema(description = "企业英文名")
	@TableField("enterprise_name_en")
	private String enterpriseNameEn;

	@Schema(description = "企业唯一标识")
	private String uniqued;

	@Schema(description = "企业Logo")
	private String logo;

	@Schema(description = "省份")
	private String province;

	@Schema(description = "城市")
	private String city;

	@Schema(description = "区域")
	private String area;

	@Schema(description = "详细地址")
	private String address;

	@Schema(description = "联系电话")
	private String phone;

	@Schema(description = "所属用户 UID")
	private String uid;

	@Schema(description = "公司简称")
	@TableField("short_name")
	private String shortName;

	@Schema(description = "软删除时间（PHP 列名 delete）")
	@TableField("`delete`")
	private LocalDateTime deleteTime;

	@Schema(description = "状态：1启用 0禁用")
	private Integer status;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

}
