package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.service.EnterprisePaytypeService;
import com.bubblecloud.common.core.util.PojoConvertUtil;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.oa.api.dto.finance.EnterprisePaytypeSaveDTO;
import com.bubblecloud.oa.api.entity.EnterprisePaytype;
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
import org.springframework.web.bind.annotation.RestController;

/**
 * 支付方式（PHP {@code ent/pay_type} → eb_enterprise_paytype，W-23）。
 *
 * @author qinlei
 * @date 2026/4/6 16:00
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/pay_type")
@Tag(name = "支付方式")
public class PayTypeController {

	private final EnterprisePaytypeService enterprisePaytypeService;

	@GetMapping({ "", "/page" })
	@Operation(summary = "支付方式列表")
	public R<SimplePageVO> page(@ParameterObject Pg pg, @ParameterObject EnterprisePaytype query) {
		Page<EnterprisePaytype> res = enterprisePaytypeService.findPg(pg, query);
		return R.phpOk(SimplePageVO.of((int) res.getCurrent(), (int) res.getSize(), res.getTotal(), res.getRecords()));
	}

	@GetMapping("/create")
	@Operation(summary = "创建表单（占位）")
	public R<EnterprisePaytype> create() {
		return R.phpOk(new EnterprisePaytype());
	}

	@PostMapping
	@Operation(summary = "保存支付方式")
	public R<String> store(@RequestBody EnterprisePaytypeSaveDTO dto) {
		EnterprisePaytype e = PojoConvertUtil.convertPojo(dto, EnterprisePaytype.class);
		enterprisePaytypeService.create(e);
		return R.phpOk(OaConstants.INSERT_SUCC);
	}

	@GetMapping("/{id}")
	@Operation(summary = "支付方式详情/状态")
	public R<EnterprisePaytype> show(@PathVariable Long id) {
		return R.phpOk(enterprisePaytypeService.getById(id));
	}

	@GetMapping("/{id}/edit")
	@Operation(summary = "编辑表单数据")
	public R<EnterprisePaytype> edit(@PathVariable Long id) {
		return R.phpOk(enterprisePaytypeService.getById(id));
	}

	@PutMapping("/{id}")
	@Operation(summary = "修改支付方式")
	public R<String> update(@PathVariable Long id, @RequestBody EnterprisePaytypeSaveDTO dto) {
		EnterprisePaytype e = PojoConvertUtil.convertPojo(dto, EnterprisePaytype.class);
		e.setId(id);
		enterprisePaytypeService.update(e);
		return R.phpOk(OaConstants.UPDATE_SUCC);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除支付方式")
	public R<String> destroy(@PathVariable Long id) {
		enterprisePaytypeService.removeById(id);
		return R.phpOk(OaConstants.DELETE_SUCC);
	}

}
