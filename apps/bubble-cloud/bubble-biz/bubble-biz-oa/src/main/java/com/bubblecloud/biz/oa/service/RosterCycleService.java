package com.bubblecloud.biz.oa.service;

import java.util.List;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.dto.attendance.RosterCycleSaveDTO;
import com.bubblecloud.oa.api.entity.RosterCycle;
import com.bubblecloud.oa.api.vo.attendance.RosterCycleListRowVO;

/**
 * 排班周期（对齐 PHP {@code ent/attendance/cycle}）。
 *
 * @author qinlei
 * @date 2026/4/7 12:15
 */
public interface RosterCycleService extends UpService<RosterCycle> {

	List<RosterCycleListRowVO> listByGroup(Integer groupId);

	Integer saveCycle(Long uid, RosterCycleSaveDTO dto);

	void updateCycle(Integer id, Long uid, RosterCycleSaveDTO dto);

	void deleteCycle(Integer id);

	RosterCycleListRowVO getDetail(Integer groupId, Integer id);

	List<RosterCycleListRowVO> listForArrange(Integer groupId);

}
