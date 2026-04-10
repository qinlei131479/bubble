package com.bubblecloud.oa.api.vo.attendance;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 排班列表中的考勤组摘要（对齐 PHP group 关联）。
 *
 * @author qinlei
 * @date 2026/4/7 12:15
 */
@Data
@Schema(description = "排班列表考勤组摘要")
public class AttendanceArrangeGroupStubVO {

	@JsonProperty("id")
	private Integer id;

	@JsonProperty("type")
	private Integer type;

	@JsonProperty("name")
	private String name;

	@JsonProperty("is_delete")
	private Integer isDelete;

	@JsonProperty("members")
	private List<OaIdNameVO> members = Collections.emptyList();

}
