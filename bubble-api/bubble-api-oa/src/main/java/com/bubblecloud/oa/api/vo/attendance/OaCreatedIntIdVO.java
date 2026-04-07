package com.bubblecloud.oa.api.vo.attendance;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 新建成功返回主键 id（对齐 PHP data.id）。
 *
 * @author qinlei
 * @date 2026/4/7 12:15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "新建记录ID")
public class OaCreatedIntIdVO {

	@JsonProperty("id")
	private Integer id;

}
