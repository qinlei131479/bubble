package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.biz.oa.service.ProgramOaService;
import com.bubblecloud.biz.oa.util.OaSecurityUtil;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.oa.api.dto.program.ProgramSaveDTO;
import com.bubblecloud.oa.api.entity.Admin;
import com.bubblecloud.oa.api.entity.Program;
import com.bubblecloud.oa.api.vo.CreatedIdVO;
import com.bubblecloud.oa.api.vo.ListCountVO;
import com.bubblecloud.oa.api.vo.program.ProgramListItemVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 项目（PHP {@code ent/program}，W-24）。
 *
 * @author qinlei
 * @date 2026/4/6 16:00
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/program")
@Tag(name = "项目管理")
public class ProgramController {

	private final ProgramOaService programOaService;

	@GetMapping({ "", "/" })
	@Operation(summary = "项目列表")
	public R<ListCountVO<ProgramListItemVO>> index(@ParameterObject Pg pg, Program query) {
		Long op = OaSecurityUtil.currentUserId();
		return R.phpOk(programOaService.index(pg, query, ObjectUtil.defaultIfNull(op, 0L)));
	}

	@GetMapping("/select")
	@Operation(summary = "项目下拉")
	public R<java.util.List<Program>> select(@RequestParam(required = false) Long uid) {
		Program q = new Program();
		if (ObjectUtil.isNotNull(uid)) {
			q.setUid(uid);
		}
		Long op = OaSecurityUtil.currentUserId();
		return R.phpOk(programOaService.selectOptions(q, ObjectUtil.defaultIfNull(op, 0L)));
	}

	@GetMapping("/members")
	@Operation(summary = "项目成员")
	public R<java.util.List<Admin>> members(@RequestParam Long programId) {
		return R.phpOk(programOaService.memberCards(programId));
	}

	@PostMapping
	@Operation(summary = "保存项目")
	public R<CreatedIdVO> store(@RequestBody ProgramSaveDTO dto) {
		try {
			Long op = OaSecurityUtil.currentUserId();
			Program p = programOaService.save(dto, ObjectUtil.defaultIfNull(op, 0L));
			return R.phpOk(new CreatedIdVO(p.getId()));
		}
		catch (Exception e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@PutMapping("/{id}")
	@Operation(summary = "修改项目")
	public R<String> update(@PathVariable Long id, @RequestBody ProgramSaveDTO dto) {
		try {
			Long op = OaSecurityUtil.currentUserId();
			programOaService.update(id, dto, ObjectUtil.defaultIfNull(op, 0L));
			return R.phpOk("common.update.succ");
		}
		catch (Exception e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@GetMapping("/info/{id}")
	@Operation(summary = "项目详情")
	public R<Program> info(@PathVariable Long id) {
		try {
			return R.phpOk(programOaService.getInfo(id));
		}
		catch (Exception e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除项目")
	public R<String> destroy(@PathVariable Long id) {
		try {
			Long op = OaSecurityUtil.currentUserId();
			programOaService.delete(id, ObjectUtil.defaultIfNull(op, 0L));
			return R.phpOk("common.delete.succ");
		}
		catch (Exception e) {
			return R.phpFailed(e.getMessage());
		}
	}

}
