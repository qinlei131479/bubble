package com.bubblecloud.biz.oa.controller;

import java.util.Collections;

import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.oa.api.vo.SimplePageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 云盘空间（PHP {@code ent/cloud/space}，W-26 占位）。
 *
 * @author qinlei
 * @date 2026/4/6 16:30
 */
@RestController
@RequestMapping("/ent/cloud/space")
@Tag(name = "云盘空间占位")
public class CloudSpaceWave7Controller {

	@GetMapping({ "", "/page" })
	@Operation(summary = "空间列表占位")
	public R<SimplePageVO> page(@ParameterObject Pg pg) {
		return R.phpOk(SimplePageVO.of((int) pg.getCurrent(), (int) pg.getSize(), 0, Collections.emptyList()));
	}

}
