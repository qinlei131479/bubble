package com.bubblecloud.oa.api.vo.company;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 员工档案列表单行（对齐前端 {@code hr/archives} 表格字段）。
 *
 * @author qinlei
 */
@Data
@Schema(description = "员工档案列表项")
public class CompanyCardListItemVO {

	private Long id;

	private String uid;

	private String name;

	private String phone;

	private String avatar;

	private Integer status;

	private Integer sex;

	private String education;

	@JsonProperty("is_part")
	private Integer isPart;

	private Integer type;

	@JsonProperty("interview_position")
	private String interviewPosition;

	@JsonProperty("work_time")
	private String workTime;

	@JsonProperty("quit_time")
	private String quitTime;

	@JsonProperty("formal_time")
	private String formalTime;

	@JsonProperty("interview_date")
	private String interviewDate;

	private CompanyCardJobVO job;

	private List<CompanyCardFrameVO> frames = new ArrayList<>();

}
