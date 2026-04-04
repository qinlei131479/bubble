package com.bubblecloud.biz.oa.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.service.ContractResourceCrmService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.oa.api.entity.ContractResource;
import com.bubblecloud.oa.api.vo.SimplePageVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import cn.hutool.core.util.ObjectUtil;

/**
 * 合同附件（对齐 PHP {@code ent/client/resources}）。
 *
 * @author qinlei
 * @date 2026/4/3 13:00
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/client/resources")
@Tag(name = "CRM合同附件")
public class CrmContractResourceController {

	private final ContractResourceCrmService contractResourceCrmService;

	@GetMapping({"", "/"})
	@Operation(summary = "合同附件列表")
	public R<SimplePageVO> index(@ParameterObject Pg pg, @RequestParam(required = false) Integer cid,
			@RequestParam(required = false) Integer entid) {
		ContractResource q = new ContractResource();
		q.setCid(cid);
		q.setEntid(ObjectUtil.defaultIfNull(entid, 0));
		Page<ContractResource> res = contractResourceCrmService.findPg(pg, q);
		return R.phpOk(SimplePageVO.of((int) res.getCurrent(), (int) res.getSize(), res.getTotal(), res.getRecords()));
	}

	@PostMapping({"", "/"})
	@Operation(summary = "保存合同附件")
	public R<String> store(@RequestBody ContractResource body) {
		contractResourceCrmService.create(body);
		return R.phpOk(OaConstants.INSERT_SUCC);
	}

	@PutMapping("/{id}")
	@Operation(summary = "修改合同附件")
	public R<String> update(@PathVariable Long id, @RequestBody ContractResource body) {
		body.setId(id);
		contractResourceCrmService.update(body);
		return R.phpOk(OaConstants.UPDATE_SUCC);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除合同附件")
	public R<String> destroy(@PathVariable Long id) {
		contractResourceCrmService.removeById(id);
		return R.phpOk(OaConstants.DELETE_SUCC);
	}

	@GetMapping("/info/{id}")
	@Operation(summary = "合同附件详情")
	public R<ContractResource> info(@PathVariable Long id) {
		return R.phpOk(contractResourceCrmService.getById(id));
	}

}
