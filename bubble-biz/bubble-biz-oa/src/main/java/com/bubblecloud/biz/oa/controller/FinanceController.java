package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.vo.SimplePageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 财务（占位，待按 eb_bill_list 等表实现）。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@RestController
@RequestMapping("/ent/finance")
@Tag(name = "财务管理")
public class FinanceController {

	@GetMapping("/bill/page")
	@Operation(summary = "账单分页")
	public R<SimplePageVO> page(@RequestParam(defaultValue = "1") Integer current,
								@RequestParam(defaultValue = "20") Integer size) {
		return R.phpOk(SimplePageVO.empty(current, size));
	}

}
