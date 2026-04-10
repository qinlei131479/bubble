package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.service.EmployeeTrainService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.dto.EmployeeTrainUpdateDTO;
import com.bubblecloud.oa.api.entity.EmployeeTrain;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 员工培训（对齐 PHP {@code ent/company/train}）。
 *
 * @author qinlei
 * @date 2026/3/29 下午6:30
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/company/train")
@Tag(name = "员工培训")
public class EmployeeTrainController {

	private final EmployeeTrainService employeeTrainService;

	@GetMapping("/{type}")
	@Operation(summary = "员工培训详情")
	public R<EmployeeTrain> info(@PathVariable String type) {
		return R.phpOk(employeeTrainService.getInfo(type));
	}

	@PutMapping("/{type}")
	@Operation(summary = "更新培训内容")
	public R<String> update(@PathVariable String type, @RequestBody EmployeeTrainUpdateDTO dto) {
		employeeTrainService.updateTrainContent(type, dto == null ? null : dto.getContent());
		return R.phpOk(OaConstants.OPT_SUCC);
	}

}
