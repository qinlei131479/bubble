package com.bubblecloud.biz.oa.controller;

import java.util.Collections;

import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.oa.api.vo.SimplePageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 云盘文件（PHP {@code ent/cloud/file/{fid}}，W-26 占位）。
 *
 * @author qinlei
 * @date 2026/4/6 16:30
 */
@RestController
@RequestMapping("/ent/cloud/file/{fid}")
@Tag(name = "云盘文件占位")
public class CloudFileWave7Controller {

	@GetMapping({ "", "/list" })
	@Operation(summary = "文件列表占位")
	public R<SimplePageVO> list(@PathVariable Long fid, @ParameterObject Pg pg) {
		return R.phpOk(SimplePageVO.of((int) pg.getCurrent(), (int) pg.getSize(), 0, Collections.emptyList()));
	}

}
