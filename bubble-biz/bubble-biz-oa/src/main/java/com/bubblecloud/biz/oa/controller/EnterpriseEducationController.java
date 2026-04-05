package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.biz.oa.service.EnterpriseUserEducationBizService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.dto.enterprise.EnterpriseUserEducationSaveDTO;
import com.bubblecloud.oa.api.entity.EnterpriseUserEducation;
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
 * 企业员工教育经历（对齐 PHP {@code ent/education}）。
 * @author qinlei
 * @date 2026/4/5
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/education")
@Tag(name = "企业员工教育经历")
public class EnterpriseEducationController {

	private final EnterpriseUserEducationBizService enterpriseUserEducationBizService;

	private final ObjectMapper objectMapper;

	@GetMapping(value = { "", "/page" })
	@Operation(summary = "教育经历列表")
	public R<ListCountVO<EnterpriseUserEducation>> index(@RequestParam("user_id") Long userId) {
		return R.phpOk(enterpriseUserEducationBizService.list(userId));
	}

	@GetMapping("/create")
	@Operation(summary = "新增教育经历表单")
	public R<OaElFormVO> createForm(@RequestParam("user_id") Long userId) {
		try {
			return R.phpOk(enterpriseUserEducationBizService.createForm(userId, objectMapper));
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@PostMapping
	@Operation(summary = "保存教育经历")
	public R<DictTypeStoreResultVO> store(@RequestBody EnterpriseUserEducationSaveDTO dto) {
		try {
			Long id = enterpriseUserEducationBizService.save(dto);
			return R.phpOk(new DictTypeStoreResultVO(id));
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@GetMapping("/{id}/edit")
	@Operation(summary = "编辑教育经历表单")
	public R<OaElFormVO> editForm(@PathVariable Long id) {
		try {
			return R.phpOk(enterpriseUserEducationBizService.editForm(id, objectMapper));
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@PutMapping("/{id}")
	@Operation(summary = "更新教育经历")
	public R<String> update(@PathVariable Long id, @RequestBody EnterpriseUserEducationSaveDTO dto) {
		try {
			enterpriseUserEducationBizService.update(id, dto);
			return R.phpOk("修改成功");
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除教育经历")
	public R<String> destroy(@PathVariable Long id) {
		try {
			enterpriseUserEducationBizService.remove(id);
			return R.phpOk("删除成功");
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

}
