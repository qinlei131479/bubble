package com.bubblecloud.biz.oa.service;

import com.bubblecloud.oa.api.dto.program.ProgramTaskCommentSaveDTO;
import com.bubblecloud.oa.api.vo.CreatedIdVO;
import com.bubblecloud.oa.api.vo.program.ProgramCommentListVO;

/**
 * 任务评论（对齐 PHP ProgramTaskCommentService）。
 *
 * @author qinlei
 * @date 2026/4/8 13:30
 */
public interface ProgramTaskCommentOaService {

	ProgramCommentListVO listByTask(Long taskId);

	CreatedIdVO save(ProgramTaskCommentSaveDTO dto, Long operatorUid);

	void update(Long id, ProgramTaskCommentSaveDTO dto, Long operatorUid);

	void delete(Long id, Long operatorUid);

}
