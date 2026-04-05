package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.biz.oa.service.UserWorkHistoryService;
import com.bubblecloud.biz.oa.util.OaSecurityUtil;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.dto.resume.UserWorkHistorySaveDTO;
import com.bubblecloud.oa.api.entity.UserWorkHistory;
import com.bubblecloud.oa.api.vo.ListCountVO;
import com.bubblecloud.oa.api.vo.config.DictTypeStoreResultVO;
import com.bubblecloud.oa.api.vo.form.OaElFormVO;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 个人简历工作经历（与 {@link EnterpriseUserDailyController} 共用前缀 {@code /ent/user/work}）。
 * @author qinlei
 * @date 2026/4/5
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/user/work")
@Tag(name = "个人简历工作经历")
public class UserWorkHistoryController {

	private final UserWorkHistoryService userWorkHistoryService;

	private final ObjectMapper objectMapper;

	@GetMapping(value = { "", "/page" })
	@Operation(summary = "工作经历列表")
	public R<ListCountVO<UserWorkHistory>> index(@RequestParam(value = "resume_id", required = false) Long resumeId) {
		return R.phpOk(userWorkHistoryService.list(OaSecurityUtil.currentUserId(), resumeId));
	}

	@GetMapping("/create")
	@Operation(summary = "新增工作经历表单")
	public R<OaElFormVO> createForm() {
		return R.phpOk(userWorkHistoryService.createForm(OaSecurityUtil.currentUserId(), objectMapper));
	}

	@PostMapping
	@Operation(summary = "保存工作经历")
	public R<DictTypeStoreResultVO> store(@RequestBody UserWorkHistorySaveDTO dto) {
		try {
			Long id = userWorkHistoryService.save(OaSecurityUtil.currentUserId(), dto);
			return R.phpOk(new DictTypeStoreResultVO(id));
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@GetMapping("/{id}/edit")
	@Operation(summary = "编辑工作经历表单")
	public R<OaElFormVO> editForm(@PathVariable Long id) {
		try {
			return R.phpOk(userWorkHistoryService.editForm(OaSecurityUtil.currentUserId(), id, objectMapper));
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@PutMapping("/{id}")
	@Operation(summary = "更新工作经历")
	public R<String> update(@PathVariable Long id, @RequestBody UserWorkHistorySaveDTO dto) {
		try {
			userWorkHistoryService.update(OaSecurityUtil.currentUserId(), id, dto);
			return R.phpOk("修改成功");
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除工作经历")
	public R<String> destroy(@PathVariable Long id) {
		try {
			userWorkHistoryService.remove(OaSecurityUtil.currentUserId(), id);
			return R.phpOk("删除成功");
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

}
