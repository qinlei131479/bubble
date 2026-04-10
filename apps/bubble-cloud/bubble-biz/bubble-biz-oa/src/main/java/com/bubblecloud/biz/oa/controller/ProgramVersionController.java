package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.biz.oa.service.ProgramVersionOaService;
import com.bubblecloud.biz.oa.util.OaSecurityUtil;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.dto.program.ProgramVersionSaveBodyDTO;
import com.bubblecloud.oa.api.entity.ProgramVersion;
import cn.hutool.core.util.ObjectUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 项目版本（PHP {@code ent/program_version}）。
 *
 * @author qinlei
 * @date 2026/4/8 16:00
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/program_version")
@Tag(name = "项目版本")
public class ProgramVersionController {

	private final ProgramVersionOaService programVersionOaService;

	@GetMapping({ "", "/" })
	@Operation(summary = "版本列表")
	public R<List<ProgramVersion>> getVersion(@RequestParam(defaultValue = "0") Long programId) {
		Long op = OaSecurityUtil.currentUserId();
		return R.phpOk(programVersionOaService.listByProgram(programId, ObjectUtil.defaultIfNull(op, 0L)));
	}

	@PostMapping("/{id}")
	@Operation(summary = "保存版本")
	public R<String> setVersion(@PathVariable Long id, @RequestBody ProgramVersionSaveBodyDTO body) {
		Long op = OaSecurityUtil.currentUserId();
		programVersionOaService.saveVersions(id, body.getData(), ObjectUtil.defaultIfNull(op, 0L));
		return R.phpOk("common.operation.succ");
	}

	@GetMapping("/select")
	@Operation(summary = "版本下拉")
	public R<List<ProgramVersion>> select(@RequestParam(defaultValue = "0") Long programId) {
		Long op = OaSecurityUtil.currentUserId();
		return R.phpOk(programVersionOaService.selectList(programId, ObjectUtil.defaultIfNull(op, 0L)));
	}

}
