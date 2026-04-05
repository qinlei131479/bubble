package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.biz.oa.service.EnterpriseUserWorkBizService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.dto.enterprise.EnterpriseUserWorkSaveDTO;
import com.bubblecloud.oa.api.entity.EnterpriseUserWork;
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
 * 企业员工工作经历（对齐 PHP {@code ent/work}）。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/work")
@Tag(name = "企业员工工作经历")
public class EnterpriseWorkController {

	private final EnterpriseUserWorkBizService enterpriseUserWorkBizService;

	private final ObjectMapper objectMapper;

	@GetMapping(value = { "", "/page" })
	@Operation(summary = "工作经历列表")
	public R<ListCountVO<EnterpriseUserWork>> index(@RequestParam("user_id") Long userId) {
		return R.phpOk(enterpriseUserWorkBizService.list(userId));
	}

	@GetMapping("/create")
	@Operation(summary = "新增工作经历表单")
	public R<OaElFormVO> createForm(@RequestParam("user_id") Long userId) {
		try {
			return R.phpOk(enterpriseUserWorkBizService.createForm(userId, objectMapper));
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@PostMapping
	@Operation(summary = "保存工作经历")
	public R<DictTypeStoreResultVO> store(@RequestBody EnterpriseUserWorkSaveDTO dto) {
		try {
			Long id = enterpriseUserWorkBizService.save(dto);
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
			return R.phpOk(enterpriseUserWorkBizService.editForm(id, objectMapper));
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@PutMapping("/{id}")
	@Operation(summary = "更新工作经历")
	public R<String> update(@PathVariable Long id, @RequestBody EnterpriseUserWorkSaveDTO dto) {
		try {
			enterpriseUserWorkBizService.update(id, dto);
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
			enterpriseUserWorkBizService.remove(id);
			return R.phpOk("删除成功");
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

}
