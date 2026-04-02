package com.bubblecloud.oa.api.vo.enterprise;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 企业工作台统计数量（PHP {@code getQuantity}）。
 *
 * @author qinlei
 * @date 2026/3/29 下午5:30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "统计数量")
public class EnterpriseQuantityVO {

	@JsonProperty("num")
	@Schema(description = "数量")
	private long num;

}
