package com.bubblecloud.oa.api.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bubblecloud.common.mybatis.base.Req;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 考勤日历配置，对应 eb_calendar_config 表。
 *
 * @author qinlei
 * @date 2026/4/2 14:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "考勤日历配置")
@TableName("eb_calendar_config")
public class CalendarConfig extends Req<CalendarConfig> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "日期 YYYY-MM-DD")
	private String day;

	@Schema(description = "是否休息：0 上班；1 休息")
	@TableField("is_rest")
	private Integer isRest;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

}
