package com.bubblecloud.biz.oa.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.service.EnterpriseLogService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.oa.api.entity.EnterpriseLog;
import com.bubblecloud.oa.api.vo.SimplePageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统管理-操作日志（对齐 PHP {@code ent/system/log}，默认 eb_enterprise_log_1）。
 *
 * @author qinlei
 * @date 2026/3/30
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/system/log")
@Tag(name = "系统日志")
public class EnterpriseLogController {

	private final EnterpriseLogService enterpriseLogService;

	@GetMapping(value = { "", "/page" })
	@Operation(summary = "日志分页")
	public R<SimplePageVO> page(@ParameterObject Pg pg, @ParameterObject EnterpriseLog query,
			@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "20") int limit) {
		pg.setCurrent(pageNum);
		pg.setSize(limit);
		Page<EnterpriseLog> res = enterpriseLogService.findPg(pg, query);
		return R.phpOk(SimplePageVO.of((int) res.getCurrent(), (int) res.getSize(), res.getTotal(), res.getRecords()));
	}

}
