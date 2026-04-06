package com.bubblecloud.biz.oa.controller;

import java.util.Collections;

import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.vo.ListCountVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 工作汇报（占位，待按 eb_enterprise_user_daily 等表实现）。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@RestController
@RequestMapping("/ent/report")
@Tag(name = "工作汇报")
public class ReportController {

	@GetMapping("/page")
	@Operation(summary = "汇报分页")
	public R<ListCountVO<Object>> page(@RequestParam(defaultValue = "1") Integer current,
			@RequestParam(defaultValue = "20") Integer size) {
		return R.phpOk(ListCountVO.of(Collections.emptyList(), 0));
	}

}
