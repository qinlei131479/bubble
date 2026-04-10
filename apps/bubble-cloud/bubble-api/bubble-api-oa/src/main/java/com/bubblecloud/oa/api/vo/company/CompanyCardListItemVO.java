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
 * @date 2026/4/5
 */
@Data
@Schema(description = "员工档案列表项")
public class CompanyCardListItemVO {

	@Schema(description = "员工主键ID")
	private Long id;

	@Schema(description = "用户UID")
	private String uid;

	@Schema(description = "姓名")
	private String name;

	@Schema(description = "手机号")
	private String phone;

	@Schema(description = "头像")
	private String avatar;

	@Schema(description = "状态")
	private Integer status;

	@Schema(description = "性别")
	private Integer sex;

	@Schema(description = "学历")
	private String education;

	@Schema(description = "是否兼职")
	@JsonProperty("is_part")
	private Integer isPart;

	@Schema(description = "员工状态类型")
	private Integer type;

	@Schema(description = "面试岗位")
	@JsonProperty("interview_position")
	private String interviewPosition;

	@Schema(description = "入职时间")
	@JsonProperty("work_time")
	private String workTime;

	@Schema(description = "离职时间")
	@JsonProperty("quit_time")
	private String quitTime;

	@Schema(description = "转正时间")
	@JsonProperty("formal_time")
	private String formalTime;

	@Schema(description = "面试日期")
	@JsonProperty("interview_date")
	private String interviewDate;

	@Schema(description = "职位信息")
	private CompanyCardJobVO job;

	@Schema(description = "所属部门列表")
	private List<CompanyCardFrameVO> frames = new ArrayList<>();

}
