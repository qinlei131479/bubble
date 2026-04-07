package com.bubblecloud.biz.oa.service;

import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.dto.attendance.AttendanceArrangeBatchUpdateDTO;
import com.bubblecloud.oa.api.dto.attendance.AttendanceArrangeMonthInitDTO;
import com.bubblecloud.oa.api.entity.AttendanceArrange;
import com.bubblecloud.oa.api.vo.ListCountVO;
import com.bubblecloud.oa.api.vo.attendance.AttendanceArrangeInfoVO;
import com.bubblecloud.oa.api.vo.attendance.AttendanceArrangeListRowVO;

/**
 * 考勤排班（对齐 PHP {@code ent/attendance/arrange}）。
 *
 * @author qinlei
 * @date 2026/4/7 12:15
 */
public interface AttendanceArrangeService extends UpService<AttendanceArrange> {

	ListCountVO<AttendanceArrangeListRowVO> listPage(Pg<AttendanceArrange> pg, String name, String timeFilter);

	void saveMonth(Long uid, AttendanceArrangeMonthInitDTO dto);

	void updateBatch(Integer groupId, Long uid, AttendanceArrangeBatchUpdateDTO body);

	AttendanceArrangeInfoVO getInfo(Integer groupId, String name, String date);

}
