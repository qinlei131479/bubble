package com.bubblecloud.oa.api.vo.workbench;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

/**
 * 工作台快捷入口项。
 *
 * @author qinlei
 * @date 2026/3/29 下午5:00
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class WorkbenchQuickItemVO {

	private Long id;

	private String name;

	private Integer cid;

	private String pcUrl;

	private String uniUrl;

	private String image;

	private Integer checked;

	private Integer sort;

}
