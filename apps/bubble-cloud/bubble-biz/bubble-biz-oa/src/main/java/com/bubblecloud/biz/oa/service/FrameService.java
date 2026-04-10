package com.bubblecloud.biz.oa.service;

import java.util.List;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.entity.Frame;
import com.bubblecloud.oa.api.dto.FrameSaveDTO;
import com.bubblecloud.oa.api.dto.FrameUpdateDTO;
import com.bubblecloud.oa.api.vo.frame.FrameAdminBriefVO;
import com.bubblecloud.oa.api.vo.frame.FrameAuthTreeNodeVO;
import com.bubblecloud.oa.api.vo.frame.FrameDepartmentTreeNodeVO;
import com.bubblecloud.oa.api.vo.frame.FrameDetailVO;
import com.bubblecloud.oa.api.vo.frame.FrameFormDataVO;
import com.bubblecloud.oa.api.vo.frame.FrameScopeItemVO;
import com.bubblecloud.oa.api.vo.frame.FrameUserTreeNodeVO;

/**
 * 组织架构部门业务。
 *
 * @author qinlei
 * @date 2026/3/29 下午4:00
 */
public interface FrameService extends UpService<Frame> {

	List<FrameDepartmentTreeNodeVO> departmentTreeList(Integer isShow, Long entId);

	List<FrameAuthTreeNodeVO> getTree(Long userId, Long entId, boolean withRole, boolean isScope);

	List<FrameUserTreeNodeVO> getUserTree(Long userId, Long entId, boolean withRole, boolean leave);

	FrameFormDataVO getFormData(Long entId, Long frameId);

	void createDepartment(FrameSaveDTO dto);

	void updateDepartment(Long id, Long entId, FrameUpdateDTO dto);

	FrameDetailVO departmentInfo(Long id, Long entId);

	void deleteDepartment(Long id, Long entId);

	List<FrameAdminBriefVO> getFrameUsers(Integer frameId, Long entId);

	List<FrameScopeItemVO> scopeFrames(Long userId, Long entId);

}
