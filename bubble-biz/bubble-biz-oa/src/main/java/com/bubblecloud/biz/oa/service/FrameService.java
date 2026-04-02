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

	List<FrameDepartmentTreeNodeVO> departmentTreeList(Integer isShow, Integer entid);

	List<FrameAuthTreeNodeVO> getTree(Long userId, Integer entid, boolean withRole, boolean isScope);

	List<FrameUserTreeNodeVO> getUserTree(Long userId, Integer entid, boolean withRole, boolean leave);

	FrameFormDataVO getFormData(Integer entid, Long frameId);

	void createDepartment(FrameSaveDTO dto);

	void updateDepartment(Long id, Integer entid, FrameUpdateDTO dto);

	FrameDetailVO departmentInfo(Long id, Integer entid);

	void deleteDepartment(Long id, Integer entid);

	List<FrameAdminBriefVO> getFrameUsers(Integer frameId, Integer entid);

	List<FrameScopeItemVO> scopeFrames(Long userId, Integer entid);

}
