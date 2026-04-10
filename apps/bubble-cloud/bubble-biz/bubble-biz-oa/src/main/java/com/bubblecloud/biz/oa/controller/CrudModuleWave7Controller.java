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
 * 低代码动态模块（PHP {@code ent/crud/module/{name}}，W-25 占位：待拆表/触发器/视图等）。
 *
 * @author qinlei
 * @date 2026/4/6 16:30
 */
@RestController
@RequestMapping("/ent/crud/module/{name}")
@Tag(name = "低代码模块占位")
public class CrudModuleWave7Controller {

	@GetMapping({ "/list", "/page" })
	@Operation(summary = "动态实体列表占位")
	public R<SimplePageVO> list(@PathVariable String name, @ParameterObject Pg pg) {
		return R.phpOk(SimplePageVO.of((int) pg.getCurrent(), (int) pg.getSize(), 0, Collections.emptyList()));
	}

}
