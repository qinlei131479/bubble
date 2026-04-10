package com.bubblecloud.oa.api.vo.config;

import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 字典数据树节点。
 *
 * @author qinlei
 * @date 2026/3/30 下午11:05
 */
@Data
@Schema(description = "字典数据树节点")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DictDataTreeNodeVO {

	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "名称")
	private String name;

	@Schema(description = "值")
	private String value;

	@Schema(description = "上级值")
	private String pid;

	@Schema(description = "字典类型ID")
	private Integer typeId;

	@Schema(description = "状态")
	private Integer status;

	@Schema(description = "排序")
	private Integer sort;

	@Schema(description = "子节点")
	private List<DictDataTreeNodeVO> children;

}
