package com.bubblecloud.oa.api.dto;

import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 日报保存/修改（对齐 PHP getRequestFields）。
 *
 * @author qinlei
 * @date 2026/4/6 12:30
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "日报保存")
public class DailySaveDTO {

	private List<Object> finish;

	private List<Object> plan;

	private String mark;

	private Integer status;

	private Integer types;

	private String uid;

	private Long entid;

	private List<Long> attachIds;

	private List<Object> members;

}
