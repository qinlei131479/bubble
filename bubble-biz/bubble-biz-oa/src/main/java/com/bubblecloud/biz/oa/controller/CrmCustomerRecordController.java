package com.bubblecloud.biz.oa.controller;

import java.util.List;

import com.bubblecloud.biz.oa.service.CustomerRecordQueryService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.entity.CustomerRecord;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 客户记录（对齐 PHP {@code ent/client/record}）。
 *
 * @author qinlei
 * @date 2026/4/3 13:00
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/client/record")
@Tag(name = "CRM客户记录")
public class CrmCustomerRecordController {

	private final CustomerRecordQueryService customerRecordQueryService;

	@GetMapping({ "", "/" })
	@Operation(summary = "客户记录列表")
	public R<List<CustomerRecord>> index(@RequestParam Integer eid) {
		if (eid == null || eid < 1) {
			return R.phpFailed("common.empty.attrs");
		}
		return R.phpOk(customerRecordQueryService.listByEid(eid));
	}

}
