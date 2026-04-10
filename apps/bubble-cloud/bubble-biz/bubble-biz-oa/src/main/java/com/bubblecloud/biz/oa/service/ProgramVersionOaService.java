package com.bubblecloud.biz.oa.service;

import java.util.List;

import com.bubblecloud.oa.api.dto.program.ProgramVersionRowDTO;
import com.bubblecloud.oa.api.entity.ProgramVersion;

/**
 * 项目版本（对齐 PHP ProgramVersionService）。
 *
 * @author qinlei
 * @date 2026/4/8 13:00
 */
public interface ProgramVersionOaService {

	List<ProgramVersion> listByProgram(Long programId, Long operatorUid);

	void saveVersions(Long programId, List<ProgramVersionRowDTO> data, Long operatorUid);

	List<ProgramVersion> selectList(Long programId, Long operatorUid);

}
