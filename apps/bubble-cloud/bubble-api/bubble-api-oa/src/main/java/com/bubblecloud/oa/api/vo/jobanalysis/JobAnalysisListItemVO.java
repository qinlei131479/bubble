package com.bubblecloud.oa.api.vo.jobanalysis;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 工作分析列表行（对齐 PHP Admin + frames/frame/job JSON，蛇形字段）。
 *
 * @author qinlei
 * @date 2026/4/5 15:30
 */
@Data
@Schema(description = "工作分析列表行")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JobAnalysisListItemVO {

	@JsonProperty("id")
	@Schema(description = "员工 admin 主键")
	private Long id;

	@JsonProperty("name")
	@Schema(description = "姓名")
	private String name;

	@JsonProperty("updated_at")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Schema(description = "账号更新时间")
	private LocalDateTime updatedAt;

	@JsonProperty("operate")
	@Schema(description = "是否展示编辑（下级工作分析页）")
	private Boolean operate;

	@JsonProperty("job")
	@Schema(description = "职位")
	private JobRefVO job;

	@JsonProperty("frame")
	@Schema(description = "主部门")
	private FrameRefVO frame;

	@JsonProperty("frames")
	@Schema(description = "全部部门")
	private List<FrameRowVO> frames;

	@Data
	@Schema(description = "职位摘要")
	public static class JobRefVO {

		@JsonProperty("id")
		private Integer id;

		@JsonProperty("name")
		private String name;

	}

	@Data
	@Schema(description = "部门摘要（主部门）")
	public static class FrameRefVO {

		@JsonProperty("id")
		private Long id;

		@JsonProperty("name")
		private String name;

		@JsonProperty("user_count")
		private Integer userCount;

		@JsonProperty("is_mastart")
		private Integer isMastart;

		@JsonProperty("is_admin")
		private Integer isAdmin;

		@JsonProperty("superior_uid")
		private Long superiorUid;

	}

	@Data
	@Schema(description = "部门行")
	public static class FrameRowVO {

		@JsonProperty("id")
		private Long id;

		@JsonProperty("name")
		private String name;

		@JsonProperty("user_count")
		private Integer userCount;

		@JsonProperty("is_mastart")
		private Integer isMastart;

		@JsonProperty("is_admin")
		private Integer isAdmin;

		@JsonProperty("superior_uid")
		private Long superiorUid;

	}

}
