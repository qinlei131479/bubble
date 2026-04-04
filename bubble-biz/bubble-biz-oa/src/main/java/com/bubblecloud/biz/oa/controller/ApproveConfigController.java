package com.bubblecloud.biz.oa.controller;

import java.util.List;

import com.bubblecloud.biz.oa.service.ApproveConfigService;
import com.bubblecloud.biz.oa.util.OaSecurityUtil;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.oa.api.entity.Approve;
import com.bubblecloud.oa.api.vo.ListCountVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 审批配置（对齐 PHP {@code ent/approve/config}）。
 *
 * @author qinlei
 * @date 2026/4/2 15:00
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/approve/config")
@Tag(name = "审批配置")
public class ApproveConfigController {

	private final ApproveConfigService approveConfigService;

	@GetMapping({ "", "/page" })
	@Operation(summary = "审批配置列表")
	public R<ListCountVO<Approve>> index(@ParameterObject Pg pg, @ParameterObject Approve query,
			@RequestParam(required = false) String name, @RequestParam(required = false) Integer types,
			@RequestParam(required = false) Integer status, @RequestParam(required = false) Integer examine,
			@RequestParam(required = false) Long entid) {
		query.setNameLike(name);
		query.setFilterTypes(types);
		query.setFilterStatus(status);
		query.setFilterExamine(examine);
		query.setFilterEntid(entid);
		Page<Approve> res = approveConfigService.findPg(pg, query);
		return R.phpOk(ListCountVO.of(res.getRecords(), res.getTotal()));
	}

	@GetMapping("/search/{types}")
	@Operation(summary = "审批类型筛选列表（占位）")
	public R<List<Long>> search(@PathVariable Integer types) {
		return R.phpOk(approveConfigService.getSearchList(types, OaSecurityUtil.currentUserId()));
	}

}
