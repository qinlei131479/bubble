package com.bubblecloud.oa.api.vo.schedule;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * 用户待办日历外层元素（含 list 字段，对齐 PHP）。
 *
 * @author qinlei
 * @date 2026/3/29 下午5:00
 */
@Data
public class UserScheduleDayWrapperVO {

	private List<UserScheduleItemVO> list = new ArrayList<>();

}
