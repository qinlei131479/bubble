package com.bubblecloud.biz.oa.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.service.CompanySalaryService;
import com.bubblecloud.biz.oa.support.PhpResponse;
import com.bubblecloud.oa.api.dto.CompanySalarySaveDTO;
import com.bubblecloud.oa.api.entity.EnterpriseUserSalary;
import com.bubblecloud.oa.api.vo.SimplePageVO;
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
 * 调薪记录（对齐 PHP {@code ent/company/salary}）。
 *
 * @author qinlei
 * @date 2026/3/29 下午6:30
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/company/salary")
@Tag(name = "调薪记录")
public class CompanySalaryController {

	private final CompanySalaryService companySalaryService;

	@GetMapping
	@Operation(summary = "调薪记录列表")
	public PhpResponse<SimplePageVO> index(@RequestParam(defaultValue = "1") int entid,
			@RequestParam(required = false) Integer cardId,
			@RequestParam(required = false) Integer linkId,
			@RequestParam(required = false) Long id,
			@RequestParam(defaultValue = "1") long current,
			@RequestParam(defaultValue = "20") long size) {
		Page<EnterpriseUserSalary> page = new Page<>(current, size);
		Page<EnterpriseUserSalary> r = companySalaryService.pageSalary(entid, cardId, linkId, id, page);
		return PhpResponse.ok(SimplePageVO.of((int) r.getCurrent(), (int) r.getSize(), r.getTotal(), r.getRecords()));
	}

	@GetMapping("/{id}/edit")
	@Operation(summary = "获取调薪记录")
	public PhpResponse<EnterpriseUserSalary> edit(@PathVariable long id) {
		EnterpriseUserSalary e = companySalaryService.getForEdit(id);
		if (e == null) {
			return PhpResponse.failed("缺少必要参数");
		}
		return PhpResponse.ok(e);
	}

	@PostMapping
	@Operation(summary = "保存调薪记录")
	public PhpResponse<String> store(@RequestBody CompanySalarySaveDTO dto) {
		if (companySalaryService.saveSalary(dto)) {
			return PhpResponse.ok("common.insert.succ");
		}
		return PhpResponse.failed("common.insert.fail");
	}

	@PutMapping("/{id}")
	@Operation(summary = "修改调薪记录")
	public PhpResponse<String> update(@PathVariable long id, @RequestBody CompanySalarySaveDTO dto) {
		if (companySalaryService.updateSalary(id, dto)) {
			return PhpResponse.ok("common.update.succ");
		}
		return PhpResponse.failed("common.update.fail");
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除调薪记录")
	public PhpResponse<String> destroy(@PathVariable long id) {
		if (companySalaryService.removeSalary(id)) {
			return PhpResponse.ok("common.delete.succ");
		}
		return PhpResponse.failed("common.delete.fail");
	}

	@GetMapping("/last/{card_id}")
	@Operation(summary = "调薪最近记录")
	public PhpResponse<java.util.List<EnterpriseUserSalary>> lastRecord(@PathVariable("card_id") int cardId) {
		if (cardId <= 0) {
			return PhpResponse.failed("缺少必要参数");
		}
		return PhpResponse.ok(companySalaryService.lastByCardId(cardId));
	}

}
