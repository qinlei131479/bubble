package com.bubblecloud.biz.oa.service;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.dto.ScheduleTypeSaveDTO;
import com.bubblecloud.oa.api.entity.ScheduleType;
import com.bubblecloud.oa.api.vo.form.OaElFormVO;
import com.bubblecloud.oa.api.vo.schedule.ScheduleTypeVO;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * 日程类型（eb_schedule_type）。
 *
 * @author qinlei
 * @date 2026/4/6 10:00
 */
public interface ScheduleTypeService extends UpService<ScheduleType> {

	List<ScheduleTypeVO> listForUser(Long userId);

	OaElFormVO createForm(ObjectMapper om);

	OaElFormVO editForm(Long id, Long userId, ObjectMapper om);

	void saveType(Long userId, ScheduleTypeSaveDTO dto);

	void updateType(Long id, Long userId, ScheduleTypeSaveDTO dto);

	void deleteType(Long id, Long userId);

}
