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
 * CRM 客户（占位，待按 eb_customer 表实现）。
 *
 * @author qinlei
 */
@RestController
@RequestMapping("/ent/client")
@Tag(name = "客户管理")
public class ClientController {

	@GetMapping("/customer/page")
	@Operation(summary = "客户分页")
	public PhpResponse<SimplePageVO> customerPage(@RequestParam(defaultValue = "1") Integer current,
												  @RequestParam(defaultValue = "20") Integer size) {
		return PhpResponse.ok(SimplePageVO.empty(current, size));
	}

	@GetMapping("/remind/info/{id}")
	@Operation(summary = "付款提醒详情（工作台待办）")
	public PhpResponse<Map<String, Object>> remindInfo(@PathVariable long id) {
		Map<String, Object> data = new LinkedHashMap<>();
		data.put("id", id);
		data.put("eid", 0);
		data.put("cid", 0);
		data.put("types", 0);
		data.put("remind_id", 0);
		return PhpResponse.ok(data);
	}

}
