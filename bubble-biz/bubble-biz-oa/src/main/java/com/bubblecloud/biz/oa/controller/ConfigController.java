package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.biz.oa.support.PhpResponse;
import com.bubblecloud.oa.api.vo.SimplePageVO;
import com.bubblecloud.oa.api.vo.config.ClientRuleApproveVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统配置扩展（字典/表单等分页占位；组织架构请使用 {@code /ent/config/frame}）。
 *
 * @author qinlei
 */
@RestController
@RequestMapping("/ent/config")
@Tag(name = "系统配置扩展")
public class ConfigController {

	@GetMapping("/client_rule/approve/{isForm}")
	@Operation(summary = "客户审批规则（工作台合同弹窗）")
	public PhpResponse<ClientRuleApproveVO> clientRuleApprove(@PathVariable int isForm) {
		ClientRuleApproveVO vo = new ClientRuleApproveVO();
		vo.setContractRefundSwitch(0);
		vo.setContractRenewSwitch(0);
		return PhpResponse.ok(vo);
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
