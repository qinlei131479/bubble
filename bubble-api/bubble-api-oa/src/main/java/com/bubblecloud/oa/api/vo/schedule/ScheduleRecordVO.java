package com.bubblecloud.oa.api.vo.schedule;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 日程列表单项（对齐 PHP schedule 列表结构）。
 *
 * @author qinlei
 * @date 2026/3/29 20:45
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "日程记录")
public class ScheduleRecordVO {

	private Long id;

	private Long uid;

	private Long cid;

	private String color;

	private String title;

	private String content;

	private Integer allDay;

	private String startTime;

	private String endTime;

	private Integer period;

	private Integer rate;

	private String days;

	private Long linkId;

	private String failTime;

	@Schema(description = "当前用户在周期任务上的完成状态（若有）")
	private Integer finish;

	private ScheduleAdminBriefVO master;

	private ScheduleTypeCardVO type;

	private List<ScheduleAdminBriefVO> user = new ArrayList<>();

	private ScheduleRemindCardVO remind;

}
