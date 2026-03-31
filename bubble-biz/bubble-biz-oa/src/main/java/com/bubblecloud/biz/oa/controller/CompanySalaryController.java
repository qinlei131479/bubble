package com.bubblecloud.biz.oa.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.service.CompanySalaryService;
import com.bubblecloud.common.core.util.R;
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
import cn.hutool.core.util.ObjectUtil;

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

	@GetMapping(value = { "", "/page" })
	@Operation(summary = "调薪记录列表")
	public R<SimplePageVO> page(@RequestParam(defaultValue = "1") int entid,
			@RequestParam(required = false) Integer cardId, @RequestParam(required = false) Integer linkId,
			@RequestParam(required = false) Long id, @RequestParam(defaultValue = "1") long current,
			@RequestParam(defaultValue = "20") long size) {
		Page<EnterpriseUserSalary> mpPage = new Page<>(current, size);
		Page<EnterpriseUserSalary> r = companySalaryService.pageSalary(entid, cardId, linkId, id, mpPage);
		return R.phpOk(SimplePageVO.of((int) r.getCurrent(), (int) r.getSize(), r.getTotal(), r.getRecords()));
	}

	@GetMapping("/{id}/edit")
	@Operation(summary = "获取调薪记录")
	public R<EnterpriseUserSalary> details(@PathVariable long id) {
		EnterpriseUserSalary e = companySalaryService.getForEdit(id);
		if (ObjectUtil.isNull(e)) {
			return R.phpFailed("缺少必要参数");
		}
		return R.phpOk(e);
	}

	@PostMapping
	@Operation(summary = "保存调薪记录")
	public R<String> create(@RequestBody CompanySalarySaveDTO dto) {
		if (companySalaryService.saveSalary(dto)) {
			return R.phpOk("common.insert.succ");
		}
		return R.phpFailed("common.insert.fail");
	}

	@PutMapping("/{id}")
	@Operation(summary = "修改调薪记录")
	public R<String> update(@PathVariable long id, @RequestBody CompanySalarySaveDTO dto) {
		if (companySalaryService.updateSalary(id, dto)) {
			return R.phpOk("common.update.succ");
		}
		return R.phpFailed("common.update.fail");
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除调薪记录")
	public R<String> removeById(@PathVariable long id) {
		if (companySalaryService.removeSalary(id)) {
			return R.phpOk("common.delete.succ");
		}
		return R.phpFailed("common.delete.fail");
	}

	@GetMapping("/last/{card_id}")
	@Operation(summary = "调薪最近记录")
	public R<java.util.List<EnterpriseUserSalary>> lastRecord(@PathVariable("card_id") int cardId) {
		if (cardId <= 0) {
			return R.phpFailed("缺少必要参数");
		}
		return R.phpOk(companySalaryService.lastByCardId(cardId));
	}

}
