package com.bubblecloud.oa.api.vo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 自定义表单字段行（对齐 PHP 列表结构）。
 *
 * @author qinlei
 * @date 2026/3/30
 */
@Data
@Schema(description = "表单字段行")
public class FormDataItemVO {

	@Schema(description = "主键")
	private Long id;

	@Schema(description = "字段键")
	private String key;

	@Schema(description = "字段名称")
	private String keyName;

	@Schema(description = "类型")
	private String type;

	@Schema(description = "输入类型")
	private String inputType;

	@Schema(description = "分组ID")
	private Long cateId;

	@Schema(description = "是否允许删除 0 否 1 是")
	private Integer enableDelete;

	@Schema(description = "其它属性 JSON（param、value 等由前端解析）")
	private String param;

	private String dictIdent;

	private String value;

	private Integer sort;

	private Integer status;

	private Integer required;

	private Integer max;

	private Integer min;

	private Integer uniqued;

	private Integer decimalPlace;

	private Integer uploadType;

	private String placeholder;

}
