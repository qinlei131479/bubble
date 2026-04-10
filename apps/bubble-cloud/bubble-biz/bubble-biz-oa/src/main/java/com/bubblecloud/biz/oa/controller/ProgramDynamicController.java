package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.biz.oa.service.ProgramDynamicOaService;
import com.bubblecloud.biz.oa.util.OaSecurityUtil;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.oa.api.vo.program.ProgramDynamicListVO;
import cn.hutool.core.util.ObjectUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 项目动态（PHP {@code ent/program_dynamic}）。
 *
 * @author qinlei
 * @date 2026/4/8 16:00
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/program_dynamic")
@Tag(name = "项目动态")
public class ProgramDynamicController {

	private final ProgramDynamicOaService programDynamicOaService;

	@GetMapping("/program")
	@Operation(summary = "项目动态列表")
	public R<ProgramDynamicListVO> programDynamic(@ParameterObject Pg pg, @RequestParam(required = false) Long uid,
			@RequestParam(required = false) Long relationId) {
		Long filterUid = ObjectUtil.defaultIfNull(uid, 0L) > 0 ? uid : null;
		return R.phpOk(programDynamicOaService.pageProgramDynamics(pg, filterUid, relationId));
	}

	@GetMapping("/task")
	@Operation(summary = "任务动态列表")
	public R<ProgramDynamicListVO> taskDynamic(@ParameterObject Pg pg, @RequestParam(required = false) Long uid,
			@RequestParam(required = false) Long programId, @RequestParam(required = false) Long relationId) {
		Long filterUid = ObjectUtil.defaultIfNull(uid, 0L) > 0 ? uid : null;
		return R.phpOk(programDynamicOaService.pageTaskDynamics(pg, filterUid, programId, relationId));
	}

}
