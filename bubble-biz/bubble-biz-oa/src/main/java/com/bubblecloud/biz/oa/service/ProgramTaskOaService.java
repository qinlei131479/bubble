package com.bubblecloud.biz.oa.service;

import java.util.List;

import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.oa.api.dto.program.ProgramTaskBatchDTO;
import com.bubblecloud.oa.api.dto.program.ProgramTaskSortDTO;
import com.bubblecloud.oa.api.dto.program.ProgramTaskStoreDTO;
import com.bubblecloud.oa.api.dto.program.ProgramTaskSubordinateDTO;
import com.bubblecloud.oa.api.dto.program.ProgramTaskUpdateDTO;
import com.bubblecloud.oa.api.entity.ProgramTask;
import com.bubblecloud.oa.api.vo.CreatedIdVO;
import com.bubblecloud.oa.api.vo.ListCountVO;
import com.bubblecloud.oa.api.vo.program.ProgramTaskNodeVO;
import com.bubblecloud.oa.api.vo.program.ProgramTaskSelectNodeVO;

/**
 * 项目任务（对齐 PHP ProgramTaskController / ProgramTaskService）。
 *
 * @author qinlei
 * @date 2026/4/8 14:30
 */
public interface ProgramTaskOaService {

	ListCountVO<ProgramTaskNodeVO> pageTaskTree(Pg pg, ProgramTask query, Long operatorUid);

	CreatedIdVO store(ProgramTaskStoreDTO dto, Long operatorUid);

	CreatedIdVO saveSubordinate(ProgramTaskSubordinateDTO dto, Long operatorUid);

	void update(Long id, ProgramTaskUpdateDTO dto, Long operatorUid);

	void delete(Long id, Long operatorUid);

	ProgramTaskNodeVO info(Long id, Long operatorUid);

	List<ProgramTaskSelectNodeVO> select(Long programId, Long pid, Long operatorUid);

	void batchUpdate(ProgramTaskBatchDTO dto, Long operatorUid);

	void batchDelete(List<Long> ids, Long operatorUid);

	void sort(ProgramTaskSortDTO dto, Long operatorUid);

	ProgramTaskNodeVO share(String ident, Long operatorUid);

}
