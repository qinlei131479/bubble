package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.vo.SimplePageVO;
import com.bubblecloud.oa.api.vo.client.ClientRemindInfoVO;
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
 * @date 2026/3/30 18:00
 */
@RestController
@RequestMapping("/ent/client")
@Tag(name = "客户管理")
public class ClientController {

	@GetMapping("/customer/page")
	@Operation(summary = "客户分页")
	public R<SimplePageVO> customerPage(@RequestParam(defaultValue = "1") Integer current,
			@RequestParam(defaultValue = "20") Integer size) {
		return R.phpOk(SimplePageVO.empty(current, size));
	}

	@GetMapping("/remind/info/{id}")
	@Operation(summary = "付款提醒详情（工作台待办）")
	public R<ClientRemindInfoVO> remindInfo(@PathVariable long id) {
		ClientRemindInfoVO vo = new ClientRemindInfoVO();
		vo.setId(id);
		vo.setEid(0);
		vo.setCid(0);
		vo.setTypes(0);
		vo.setRemindId(0);
		return R.phpOk(vo);
	}

}
