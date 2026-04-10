package com.bubblecloud.biz.oa.service;

import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.oa.api.vo.program.ProgramDynamicListVO;

/**
 * 项目/任务动态（对齐 PHP ProgramDynamicService）。
 *
 * @author qinlei
 * @date 2026/4/8 12:30
 */
public interface ProgramDynamicOaService {

	void addLog(int types, int actionType, Long operatorUid, Long relationId, String title, String describeJson);

	ProgramDynamicListVO pageProgramDynamics(Pg pg, Long uid, Long relationId);

	ProgramDynamicListVO pageTaskDynamics(Pg pg, Long uid, Long programId, Long relationId);

}
