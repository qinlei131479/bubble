package com.bubblecloud.oa.api.dto.company;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 员工档案列表行（Admin + AdminInfo 联表查询结果）。
 *
 * @author qinlei
 * @date 2026/4/5
 */
@Data
@Schema(description = "员工档案列表行（Mapper 结果）")
public class CompanyCardListRow {

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

	@Schema(description = "岗位ID")
	private Integer job;

	@Schema(description = "性别")
	private Integer sex;

	@Schema(description = "学历")
	private String education;

	@Schema(description = "是否兼职")
	private Integer isPart;

	@Schema(description = "员工状态类型")
	private Integer type;

	@Schema(description = "面试岗位")
	private String interviewPosition;

	@Schema(description = "入职时间")
	private String workTime;

	@Schema(description = "离职时间")
	private String quitTime;

	@Schema(description = "转正时间")
	private String formalTime;

	@Schema(description = "面试日期")
	private String interviewDate;

	@Schema(description = "排序权重")
	private Integer sort;

	@Schema(description = "档案扩展创建时间")
	private LocalDateTime infoCreatedAt;

}
