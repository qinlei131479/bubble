package com.bubblecloud.oa.api.dto.attendance;

import java.util.Collections;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 考勤组第三步：补卡与外勤规则。
 *
 * @author qinlei
 * @date 2026/4/2 16:00
 */
@Data
@Schema(description = "考勤组第三步规则")
public class AttendanceGroupStepThreeDTO {

	@Schema(description = "是否允许补卡")
	private Integer repairAllowed;

	@Schema(description = "补卡类型列表")
	private List<Integer> repairType = Collections.emptyList();

	@Schema(description = "是否限制补卡时间")
	private Integer isLimitTime;

	@Schema(description = "补卡时间（天）")
	private Integer limitTime;

	@Schema(description = "是否限制补卡次数")
	private Integer isLimitNumber;

	@Schema(description = "补卡次数上限")
	private Integer limitNumber;

	@Schema(description = "是否拍照打卡")
	private Integer isPhoto;

	@Schema(description = "是否允许外勤打卡")
	private Integer isExternal;

	@Schema(description = "外勤是否必填备注")
	private Integer isExternalNote;

	@Schema(description = "外勤是否必须拍照")
	private Integer isExternalPhoto;

}
