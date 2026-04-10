package com.bubblecloud.oa.api.vo.form;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 自定义表单分组及字段列表。
 *
 * @author qinlei
 * @date 2026/3/30
 */
@Data
@Schema(description = "表单分组项")
public class FormCateListItemVO {

	private Long id;

	private String title;

	private Integer sort;

	private Integer types;

	private Integer status;

	private List<FormDataItemVO> data;

}
