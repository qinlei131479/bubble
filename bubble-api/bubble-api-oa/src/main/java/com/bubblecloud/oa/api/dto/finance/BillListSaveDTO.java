package com.bubblecloud.oa.api.dto.finance;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 财务流水保存 DTO（对齐 PHP BillRequest 核心字段）。
 *
 * @author qinlei
 * @date 2026/4/6 15:30
 */
@Data
@Schema(description = "财务流水保存")
public class BillListSaveDTO {

	@Schema(description = "分类ID")
	private Integer cateId;

	@Schema(description = "1收入 0支出")
	private Integer types;

	@Schema(description = "变动时间")
	private LocalDateTime editTime;

	@Schema(description = "备注")
	private String mark;

	@Schema(description = "金额")
	private BigDecimal num;

	@Schema(description = "支付方式ID")
	private Integer typeId;

	@Schema(description = "企业ID")
	private Long entid;

}
