package com.bubblecloud.oa.api.vo.hr;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 绩效未创建数量（对齐 PHP {@code isAbnormal} 的 compact('count')}）。
 *
 * @author qinlei
 * @date 2026/4/7 11:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "绩效未创建统计")
public class AssessAbnormalCountVO {

	@Schema(description = "未创建人数")
	private Integer count;

}
