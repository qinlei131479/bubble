package com.bubblecloud.oa.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 调薪记录保存/修改。
 *
 * @author qinlei
 * @date 2026/3/29 下午6:30
 */
@Data
@Schema(description = "调薪记录")
public class EnterpriseSalarySaveDTO {

	@JsonAlias("card_id")
	@Schema(description = "档案 card_id")
	private Integer cardId;

	@Schema(description = "变更金额")
	private BigDecimal total;

	@Schema(description = "变更说明")
	private String content;

	@Schema(description = "变更原因")
	private String mark;

	@JsonAlias("take_date")
	@Schema(description = "生效日期")
	private LocalDate takeDate;

	@Schema(description = "企业ID")
	private Integer entid;

}
