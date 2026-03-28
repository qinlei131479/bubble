package com.bubblecloud.biz.oa.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import com.bubblecloud.biz.oa.support.PhpResponse;
import com.bubblecloud.oa.api.vo.SimplePageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统配置扩展（占位，待按字典/表单等表实现）。
 *
 * @author qinlei
 */
@RestController
@RequestMapping("/ent/config")
@Tag(name = "系统配置扩展")
public class ConfigController {

	@GetMapping("/client_rule/approve/{isForm}")
	@Operation(summary = "客户审批规则（工作台合同弹窗）")
	public PhpResponse<Map<String, Object>> clientRuleApprove(@PathVariable int isForm) {
		Map<String, Object> data = new LinkedHashMap<>();
		data.put("contract_refund_switch", 0);
		data.put("contract_renew_switch", 0);
		return PhpResponse.ok(data);
	}

	@GetMapping("/dict/type/page")
	@Operation(summary = "字典类型分页")
	public PhpResponse<SimplePageVO> dictTypePage(@RequestParam(defaultValue = "1") Integer current,
												  @RequestParam(defaultValue = "20") Integer size) {
		return PhpResponse.ok(SimplePageVO.empty(current, size));
	}

	@GetMapping("/dict/data/page")
	@Operation(summary = "字典数据分页")
	public PhpResponse<SimplePageVO> dictDataPage(@RequestParam(defaultValue = "1") Integer current,
												  @RequestParam(defaultValue = "20") Integer size) {
		return PhpResponse.ok(SimplePageVO.empty(current, size));
	}

	@GetMapping("/form/page")
	@Operation(summary = "表单分页")
	public PhpResponse<SimplePageVO> formPage(@RequestParam(defaultValue = "1") Integer current,
											  @RequestParam(defaultValue = "20") Integer size) {
		return PhpResponse.ok(SimplePageVO.empty(current, size));
	}

}
