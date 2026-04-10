package com.bubblecloud.biz.oa.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.oa.api.dto.program.ProgramSaveDTO;
import com.bubblecloud.oa.api.entity.Admin;
import com.bubblecloud.oa.api.entity.Program;
import com.bubblecloud.oa.api.vo.ListCountVO;
import com.bubblecloud.oa.api.vo.program.ProgramListItemVO;

/**
 * 项目管理（PHP ent/program）业务。
 *
 * @author qinlei
 * @date 2026/4/6 16:00
 */
public interface ProgramOaService {

	ListCountVO<ProgramListItemVO> index(Page<Program> page, Program query, Long operatorUid);

	List<Program> selectOptions(Program query, Long operatorUid);

	List<Admin> memberCards(Long programId);

	Program save(ProgramSaveDTO dto, Long creatorUid);

	void update(Long id, ProgramSaveDTO dto, Long operatorUid);

	void delete(Long id, Long operatorUid);

	Program getInfo(Long id);

}
