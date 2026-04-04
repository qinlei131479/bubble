package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.service.UserMemorialService;
import com.bubblecloud.biz.oa.util.OaSecurityUtil;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.oa.api.dto.UserMemorialSaveDTO;
import com.bubblecloud.oa.api.entity.UserMemorial;
import com.bubblecloud.oa.api.vo.ListCountVO;
import com.bubblecloud.oa.api.vo.memorial.UserMemorialGroupRowVO;
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

/**
 * 备忘录（对齐 PHP {@code ent/user/memorial}）。
 *
 * @author qinlei
 * @date 2026/4/2 10:30
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/user/memorial")
@Tag(name = "备忘录")
public class UserMemorialController {

	private final UserMemorialService userMemorialService;

	@GetMapping({ "", "/page" })
	@Operation(summary = "备忘录列表")
	public R<ListCountVO<UserMemorial>> index(@ParameterObject Pg pg, @RequestParam(required = false) Integer pid,
			@RequestParam(required = false) String title) {
		return R.phpOk(userMemorialService.listPage(pg, pid, title, OaSecurityUtil.currentUserId()));
	}

	@PostMapping
	@Operation(summary = "保存备忘录")
	public R<String> create(@RequestBody UserMemorialSaveDTO dto) {
		userMemorialService.createMemorial(dto, OaSecurityUtil.currentUserId());
		return R.phpOk(OaConstants.INSERT_SUCC);
	}

	@PutMapping("/{id}")
	@Operation(summary = "修改备忘录")
	public R<String> update(@PathVariable Long id, @RequestBody UserMemorialSaveDTO dto) {
		userMemorialService.updateMemorial(id, dto, OaSecurityUtil.currentUserId());
		return R.phpOk(OaConstants.UPDATE_SUCC);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除备忘录")
	public R<String> removeById(@PathVariable Long id) {
		userMemorialService.removeMemorial(id, OaSecurityUtil.currentUserId());
		return R.phpOk(OaConstants.DELETE_SUCC);
	}

	@GetMapping("/group")
	@Operation(summary = "最新分组列表")
	public R<ListCountVO<UserMemorialGroupRowVO>> group(@ParameterObject Pg<?> pg,
			@RequestParam(required = false) Integer pid, @RequestParam(required = false) String title) {
		return R.phpOk(userMemorialService.groupPage(pg, pid, title, OaSecurityUtil.currentUserId()));
	}

}
