package com.bubblecloud.biz.oa.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.service.ClientFollowCrmService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.entity.ClientFollow;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 客户跟进（对齐 PHP {@code ent/client/follow}）。
 *
 * @author qinlei
 * @date 2026/4/3 14:00
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/client/follow")
@Tag(name = "CRM跟进")
public class CrmClientFollowController {

	private final ClientFollowCrmService clientFollowCrmService;

	@GetMapping({ "", "/" })
	@Operation(summary = "跟进列表")
	public R<List<ClientFollow>> index(@RequestParam Integer eid, @RequestParam(defaultValue = "0") Integer status) {
		return R.phpOk(clientFollowCrmService.list(Wrappers.lambdaQuery(ClientFollow.class)
			.eq(ClientFollow::getEid, eid)
			.eq(status != null && status != 0, ClientFollow::getStatus, status)
			.isNull(ClientFollow::getDeletedAt)
			.orderByDesc(ClientFollow::getId)));
	}

	@PostMapping({ "", "/" })
	@Operation(summary = "保存跟进")
	public R<String> store(@RequestBody ClientFollow body) {
		clientFollowCrmService.create(body);
		return R.phpOk(OaConstants.INSERT_SUCC);
	}

	@PutMapping("/{id}")
	@Operation(summary = "修改跟进")
	public R<String> update(@PathVariable Long id, @RequestBody ClientFollow body) {
		body.setId(id);
		clientFollowCrmService.update(body);
		return R.phpOk(OaConstants.UPDATE_SUCC);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除跟进")
	public R<String> destroy(@PathVariable Long id) {
		clientFollowCrmService.softDeleteById(id);
		return R.phpOk(OaConstants.DELETE_SUCC);
	}

}
