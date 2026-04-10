package com.bubblecloud.biz.oa.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.service.EnterpriseUserSalaryService;
import com.bubblecloud.common.core.util.PojoConvertUtil;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.oa.api.dto.EnterpriseSalarySaveDTO;
import com.bubblecloud.oa.api.entity.EnterpriseUserSalary;
import com.bubblecloud.oa.api.vo.SimplePageVO;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
public class EnterpriseUserSalaryController {

	private final EnterpriseUserSalaryService enterpriseUserSalaryService;

	@GetMapping(value = { "", "/page" })
	@Operation(summary = "调薪记录列表")
	public R<SimplePageVO> page(@ParameterObject Pg pg, @ParameterObject EnterpriseUserSalary query) {
		Page<EnterpriseUserSalary> r = enterpriseUserSalaryService.findPg(pg, query);
		return R.phpOk(SimplePageVO.of((int) r.getCurrent(), (int) r.getSize(), r.getTotal(), r.getRecords()));
	}

	@GetMapping("/{id}/edit")
	@Operation(summary = "获取调薪记录")
	public R<EnterpriseUserSalary> details(@PathVariable Long id) {
		EnterpriseUserSalary e = enterpriseUserSalaryService.getById(id);
		return R.phpOk(e);
	}

	@PostMapping
	@Operation(summary = "保存调薪记录")
	public R<String> create(@RequestBody EnterpriseSalarySaveDTO dto) {
		EnterpriseUserSalary obj = PojoConvertUtil.convertPojo(dto, EnterpriseUserSalary.class);
		enterpriseUserSalaryService.create(obj);
		return R.phpOk(OaConstants.INSERT_SUCC);
	}

	@PutMapping("/{id}")
	@Operation(summary = "修改调薪记录")
	public R<String> update(@PathVariable Long id, @RequestBody EnterpriseSalarySaveDTO dto) {
		EnterpriseUserSalary obj = PojoConvertUtil.convertPojo(dto, EnterpriseUserSalary.class);
		obj.setId(id);
		enterpriseUserSalaryService.update(obj);
		return R.phpOk(OaConstants.UPDATE_SUCC);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除调薪记录")
	public R<String> removeById(@PathVariable Long id) {
		enterpriseUserSalaryService.removeById(id);
		return R.phpOk(OaConstants.DELETE_SUCC);
	}

	@GetMapping("/last/{card_id}")
	@Operation(summary = "调薪最近记录")
	public R<List<EnterpriseUserSalary>> lastRecord(@PathVariable("card_id") Long cardId) {
		return R.phpOk(enterpriseUserSalaryService.listLastByCardId(cardId));
	}

}
