package com.bubblecloud.oa.api.vo.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 字典类型新增成功返回 id。
 *
 * @author qinlei
 * @date 2026/3/30 下午11:05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "字典类型保存结果")
public class DictTypeStoreResultVO {

	@Schema(description = "主键ID")
	private Long id;

}
