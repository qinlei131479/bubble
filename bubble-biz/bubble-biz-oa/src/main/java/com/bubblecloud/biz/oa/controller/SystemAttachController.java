package com.bubblecloud.biz.oa.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.service.SystemAttachAdminService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.oa.api.entity.SystemAttach;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统附件（对齐 PHP {@code ent/system/attach}）。
 *
 * @author qinlei
 * @date 2026/3/30
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/system/attach")
@Tag(name = "系统附件")
public class SystemAttachController {

	private final SystemAttachAdminService systemAttachAdminService;

	@GetMapping(value = {"", "/page"})
	@Operation(summary = "附件分页")
	public R<Page<SystemAttach>> page(@ParameterObject Pg pg, @ParameterObject SystemAttach query,
									  @RequestParam(defaultValue = "1") int pageNum,
									  @RequestParam(defaultValue = "20") int limit) {
		pg.setCurrent(pageNum);
		pg.setSize(limit);
		return R.phpOk(systemAttachAdminService.findPg(pg, query));
	}

}
