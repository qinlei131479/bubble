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
 * 项目管理（占位，待按 eb_program 表实现）。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@RestController
@RequestMapping("/ent/program")
@Tag(name = "项目管理")
public class ProgramController {

	@GetMapping("/page")
	@Operation(summary = "项目分页")
	public R<SimplePageVO> page(@RequestParam(defaultValue = "1") Integer current,
			@RequestParam(defaultValue = "20") Integer size) {
		return R.phpOk(SimplePageVO.empty(current, size));
	}

}
