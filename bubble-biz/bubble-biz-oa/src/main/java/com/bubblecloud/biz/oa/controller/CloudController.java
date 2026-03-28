package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.biz.oa.support.PhpResponse;
import com.bubblecloud.oa.api.vo.SimplePageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 网盘（占位，待按 eb_enterprise_file 等表实现）。
 *
 * @author qinlei
 */
@RestController
@RequestMapping("/ent/cloud")
@Tag(name = "网盘")
public class CloudController {

	@GetMapping("/space/page")
	@Operation(summary = "网盘空间分页")
	public PhpResponse<SimplePageVO> spacePage(@RequestParam(defaultValue = "1") Integer current,
											   @RequestParam(defaultValue = "20") Integer size) {
		return PhpResponse.ok(SimplePageVO.empty(current, size));
	}

}
