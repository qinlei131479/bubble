package com.bubblecloud.biz.oa.service;

import java.util.List;

import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.dto.attendance.AttendanceGroupStepFourDTO;
import com.bubblecloud.oa.api.dto.attendance.AttendanceGroupStepOneDTO;
import com.bubblecloud.oa.api.dto.attendance.AttendanceGroupStepThreeDTO;
import com.bubblecloud.oa.api.dto.attendance.AttendanceGroupStepTwoDTO;
import com.bubblecloud.oa.api.dto.attendance.AttendanceRepeatCheckDTO;
import com.bubblecloud.oa.api.dto.attendance.AttendanceWhitelistSetDTO;
import com.bubblecloud.oa.api.entity.Admin;
import com.bubblecloud.oa.api.entity.AttendanceGroup;
import com.bubblecloud.oa.api.vo.ListCountVO;
import com.bubblecloud.oa.api.vo.attendance.AttendanceGroupDetailVO;
import com.bubblecloud.oa.api.vo.attendance.AttendanceGroupListItemVO;
import com.bubblecloud.oa.api.vo.attendance.AttendanceGroupSelectItemVO;
import com.bubblecloud.oa.api.vo.attendance.AttendanceRepeatConflictVO;
import com.bubblecloud.oa.api.vo.attendance.AttendanceWhitelistVO;
import com.bubblecloud.oa.api.vo.attendance.OaIdNameVO;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * 考勤组（对齐 PHP {@code AttendanceGroupService}）。
 *
 * @author qinlei
 * @date 2026/4/2 16:00
 */
public interface AttendanceGroupService extends UpService<AttendanceGroup> {

	ListCountVO<AttendanceGroupListItemVO> listPage(Pg<AttendanceGroup> pg, String name);

	Integer saveGroup(AttendanceGroupStepOneDTO dto, Long uid);

	/**
	 * 分步修改（body 含 step：one/two/three/four，其余字段为对应 DTO）。
	 */
	void updateByStep(Long id, JsonNode body);

	void updateStepOne(Integer id, AttendanceGroupStepOneDTO dto);

	void updateStepTwo(Integer id, AttendanceGroupStepTwoDTO dto);

	void updateStepThree(Integer id, AttendanceGroupStepThreeDTO dto);

	void updateStepFour(Integer id, AttendanceGroupStepFourDTO dto);

	void deleteGroup(Long id);

	AttendanceGroupDetailVO getInfo(Long id);

	AttendanceWhitelistVO getWhitelist();

	void setWhitelist(AttendanceWhitelistSetDTO dto);

	List<AttendanceRepeatConflictVO> memberRepeatCheck(AttendanceRepeatCheckDTO dto);

	List<Admin> getUnAttendMember();

	List<AttendanceGroupSelectItemVO> getSelectList();

	List<AttendanceRepeatConflictVO> getGroupMembersByType(Integer type, Integer filterId);

	/**
	 * 考勤组成员 id+name（filter=true，对齐 PHP getGroupMember）。
	 */
	List<OaIdNameVO> listGroupMemberBriefs(Integer groupId, String nameLike);

	List<Integer> listShiftIdsByGroup(Integer groupId);

	boolean groupExists(Integer id);

	int countGroupsByIds(List<Integer> ids);

}
