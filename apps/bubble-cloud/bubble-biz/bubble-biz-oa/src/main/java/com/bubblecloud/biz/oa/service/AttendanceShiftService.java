package com.bubblecloud.biz.oa.service;

import java.util.List;

import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.dto.attendance.AttendanceShiftSaveDTO;
import com.bubblecloud.oa.api.entity.AttendanceShift;
import com.bubblecloud.oa.api.vo.ListCountVO;
import com.bubblecloud.oa.api.vo.attendance.AttendanceShiftDetailVO;
import com.bubblecloud.oa.api.vo.attendance.AttendanceShiftListRowVO;
import com.bubblecloud.oa.api.vo.attendance.AttendanceShiftSelectItemVO;

/**
 * 考勤班次（对齐 PHP {@code ent/attendance/shift}）。
 *
 * @author qinlei
 * @date 2026/4/7 12:15
 */
public interface AttendanceShiftService extends UpService<AttendanceShift> {

	ListCountVO<AttendanceShiftListRowVO> listPage(Pg<AttendanceShift> pg, String name, Integer groupId);

	List<AttendanceShiftSelectItemVO> selectList(String name, Integer groupId);

	Integer saveShift(Long uid, AttendanceShiftSaveDTO dto);

	void updateShift(Integer id, AttendanceShiftSaveDTO dto);

	void deleteShift(Integer id);

	AttendanceShiftDetailVO getDetail(Integer id);

}
