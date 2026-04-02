package com.bubblecloud.biz.oa.service.impl;

import java.util.ArrayList;
import java.util.List;
import com.bubblecloud.common.core.util.R;
import org.springframework.transaction.annotation.Transactional;

import com.bubblecloud.biz.oa.mapper.ScheduleMapper;
import com.bubblecloud.biz.oa.service.ScheduleApiService;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.Schedule;
import com.bubblecloud.oa.api.dto.ScheduleIndexQueryDTO;
import com.bubblecloud.oa.api.dto.ScheduleStatusUpdateDTO;
import com.bubblecloud.oa.api.dto.UserScheduleQueryDTO;
import com.bubblecloud.oa.api.vo.schedule.ScheduleRecordVO;
import com.bubblecloud.oa.api.vo.schedule.ScheduleTypeVO;
import com.bubblecloud.oa.api.vo.schedule.UserScheduleDayWrapperVO;
import org.springframework.stereotype.Service;

/**
 * 日程与待办占位实现。
 *
 * @author qinlei
 * @date 2026/3/28 15:30
 */
@Service
public class ScheduleApiServiceImpl extends UpServiceImpl<ScheduleMapper, Schedule> implements ScheduleApiService {

	@Override
	public List<ScheduleTypeVO> typeList() {
		List<ScheduleTypeVO> list = new ArrayList<>();
		list.add(typeRow(1L, "默认", "default", "#409EFF"));
		list.add(typeRow(2L, "个人提醒", "personal", "#1890FF"));
		list.add(typeRow(3L, "客户跟进", "client_track", "#19BE6B"));
		return list;
	}

	private ScheduleTypeVO typeRow(long id, String name, String key, String color) {
		return new ScheduleTypeVO(id, name, key, color, "", 1);
	}

	@Override
	public List<ScheduleRecordVO> scheduleIndex(ScheduleIndexQueryDTO body) {
		return new ArrayList<>();
	}

	@Override
	public void updateStatus(Long id, ScheduleStatusUpdateDTO body) {
		// 占位
	}

	@Override
	public List<UserScheduleDayWrapperVO> userScheduleList(UserScheduleQueryDTO query) {
		List<UserScheduleDayWrapperVO> outer = new ArrayList<>();
		outer.add(new UserScheduleDayWrapperVO());
		return outer;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(Schedule req) {
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(Schedule req) {
		return super.update(req);
	}

}
