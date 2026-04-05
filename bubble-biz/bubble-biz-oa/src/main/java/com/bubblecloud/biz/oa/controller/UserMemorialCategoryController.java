package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.service.UserMemorialCategoryBizService;
import com.bubblecloud.biz.oa.util.OaSecurityUtil;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.dto.memorial.MemorialCategorySaveDTO;
import com.bubblecloud.oa.api.vo.config.DictTypeStoreResultVO;
import com.bubblecloud.oa.api.vo.form.OaElFormVO;
import com.bubblecloud.oa.api.vo.memorial.MemorialCategoryIndexVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 备忘录分类（对齐 PHP {@code ent/user/memorial_cate}）。
 *
 * @author qinlei
 * @date 2026/4/5
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/user/memorial_cate")
@Tag(name = "备忘录分类")
public class UserMemorialCategoryController {

	private final UserMemorialCategoryBizService userMemorialCategoryBizService;

	private final ObjectMapper objectMapper;

	@GetMapping(value = { "", "/page" })
	@Operation(summary = "分类树与备忘录总数")
	public R<MemorialCategoryIndexVO> index() {
		return R.phpOk(userMemorialCategoryBizService.index(OaSecurityUtil.currentUserId()));
	}

	@GetMapping("/create/{pid}")
	@Operation(summary = "新建分类表单")
	public R<OaElFormVO> createForm(@PathVariable long pid) {
		try {
			return R
				.phpOk(userMemorialCategoryBizService.createForm(OaSecurityUtil.currentUserId(), pid, objectMapper));
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@PostMapping
	@Operation(summary = "保存分类")
	public R<DictTypeStoreResultVO> store(@RequestBody MemorialCategorySaveDTO dto) {
		try {
			Long id = userMemorialCategoryBizService.store(OaSecurityUtil.currentUserId(), dto);
			return R.phpOk(new DictTypeStoreResultVO(id));
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@GetMapping("/{id}/edit")
	@Operation(summary = "编辑分类表单")
	public R<OaElFormVO> editForm(@PathVariable long id) {
		try {
			return R.phpOk(userMemorialCategoryBizService.editForm(OaSecurityUtil.currentUserId(), id, objectMapper));
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@PutMapping("/{id}")
	@Operation(summary = "更新分类")
	public R<String> update(@PathVariable long id, @RequestBody MemorialCategorySaveDTO dto) {
		try {
			userMemorialCategoryBizService.update(OaSecurityUtil.currentUserId(), id, dto);
			return R.phpOk(OaConstants.UPDATE_SUCC);
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除分类")
	public R<String> destroy(@PathVariable long id) {
		try {
			userMemorialCategoryBizService.delete(OaSecurityUtil.currentUserId(), id);
			return R.phpOk(OaConstants.DELETE_SUCC);
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

}
