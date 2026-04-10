package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.biz.oa.service.EnterpriseUserPositionBizService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.dto.enterprise.EnterpriseUserPositionSaveDTO;
import com.bubblecloud.oa.api.entity.EnterpriseUserPosition;
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
 * 企业员工任职经历（对齐 PHP {@code ent/position}）。
 *
 * @author qinlei
 * @date 2026/4/5
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/position")
@Tag(name = "企业员工任职经历")
public class EnterprisePositionController {

	private final EnterpriseUserPositionBizService enterpriseUserPositionBizService;

	private final ObjectMapper objectMapper;

	@GetMapping(value = { "", "/page" })
	@Operation(summary = "任职经历列表")
	public R<ListCountVO<EnterpriseUserPosition>> index(@RequestParam("user_id") Long userId) {
		return R.phpOk(enterpriseUserPositionBizService.list(userId));
	}

	@GetMapping("/create")
	@Operation(summary = "新增任职经历表单")
	public R<OaElFormVO> createForm(@RequestParam("user_id") Long userId) {
		try {
			return R.phpOk(enterpriseUserPositionBizService.createForm(userId, objectMapper));
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@PostMapping
	@Operation(summary = "保存任职经历")
	public R<DictTypeStoreResultVO> store(@RequestBody EnterpriseUserPositionSaveDTO dto) {
		try {
			Long id = enterpriseUserPositionBizService.save(dto);
			return R.phpOk(new DictTypeStoreResultVO(id));
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@GetMapping("/{id}/edit")
	@Operation(summary = "编辑任职经历表单")
	public R<OaElFormVO> editForm(@PathVariable Long id) {
		try {
			return R.phpOk(enterpriseUserPositionBizService.editForm(id, objectMapper));
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@PutMapping("/{id}")
	@Operation(summary = "更新任职经历")
	public R<String> update(@PathVariable Long id, @RequestBody EnterpriseUserPositionSaveDTO dto) {
		try {
			enterpriseUserPositionBizService.update(id, dto);
			return R.phpOk("修改成功");
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除任职经历")
	public R<String> destroy(@PathVariable Long id) {
		try {
			enterpriseUserPositionBizService.remove(id);
			return R.phpOk("删除成功");
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

}
