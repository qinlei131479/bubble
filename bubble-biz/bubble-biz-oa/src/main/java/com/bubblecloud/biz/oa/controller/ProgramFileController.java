package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.biz.oa.service.ProgramFileOaService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.dto.program.ProgramAttachRenameDTO;
import com.bubblecloud.oa.api.entity.SystemAttach;
import com.bubblecloud.oa.api.vo.ListCountVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import com.bubblecloud.common.mybatis.base.Pg;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 项目附件（PHP {@code ent/program_file}）。
 *
 * @author qinlei
 * @date 2026/4/8 16:00
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/program_file")
@Tag(name = "项目附件")
public class ProgramFileController {

	private final ProgramFileOaService programFileOaService;

	@GetMapping("/index")
	@Operation(summary = "附件列表")
	public R<ListCountVO<SystemAttach>> index(@ParameterObject Pg pg, @RequestParam(defaultValue = "0") long programId,
			@RequestParam(defaultValue = "1") int entid, @RequestParam(required = false) String name) {
		int page = (int) Math.max(1L, pg.getCurrent());
		int limit = (int) (pg.getSize() > 0 ? pg.getSize() : 20L);
		return R.phpOk(programFileOaService.list(entid, programId, name, page, limit));
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除附件")
	public R<String> delete(@PathVariable Integer id, @RequestParam(defaultValue = "1") int entid) {
		programFileOaService.delete(id, entid);
		return R.phpOk("common.delete.succ");
	}

	@PutMapping("/real_name/{id}")
	@Operation(summary = "重命名")
	public R<String> realName(@PathVariable Integer id, @RequestParam(defaultValue = "1") int entid,
			@RequestBody ProgramAttachRenameDTO body) {
		String rn = body == null ? "" : body.getRealName();
		programFileOaService.rename(id, entid, rn);
		return R.phpOk("common.operation.succ");
	}

}
