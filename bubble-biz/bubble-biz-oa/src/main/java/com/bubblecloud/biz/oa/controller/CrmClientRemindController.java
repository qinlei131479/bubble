package com.bubblecloud.biz.oa.controller;

import java.util.Map;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.service.ClientRemindCrmService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.oa.api.entity.ClientRemind;
import com.bubblecloud.oa.api.vo.SimplePageVO;
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
 * 付款提醒（对齐 PHP {@code ent/client/remind}，含工作台 {@code client/remind/info/{id}}）。
 *
 * @author qinlei
 * @date 2026/4/3 14:00
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/client/remind")
@Tag(name = "CRM付款提醒")
public class CrmClientRemindController {

	private final ClientRemindCrmService clientRemindCrmService;

	@GetMapping({ "", "/" })
	@Operation(summary = "付款提醒列表")
	public R<SimplePageVO> index(@ParameterObject Pg pg, @RequestParam(required = false) Integer eid,
			@RequestParam(required = false) Integer cid) {
		ClientRemind q = new ClientRemind();
		q.setEid(eid);
		q.setCid(cid);
		Page<ClientRemind> page = clientRemindCrmService.findPg(pg, q);
		return R
			.phpOk(SimplePageVO.of((int) page.getCurrent(), (int) page.getSize(), page.getTotal(), page.getRecords()));
	}

	@PostMapping({ "", "/" })
	@Operation(summary = "保存付款提醒")
	public R<String> store(@RequestBody ClientRemind body) {
		clientRemindCrmService.create(body);
		return R.phpOk(OaConstants.INSERT_SUCC);
	}

	@PutMapping("/{id}")
	@Operation(summary = "修改付款提醒")
	public R<String> update(@PathVariable Long id, @RequestBody ClientRemind body) {
		body.setId(id);
		clientRemindCrmService.update(body);
		return R.phpOk(OaConstants.UPDATE_SUCC);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除付款提醒")
	public R<String> destroy(@PathVariable Long id) {
		clientRemindCrmService.deleteById(id);
		return R.phpOk(OaConstants.DELETE_SUCC);
	}

	@PutMapping("/mark/{id}")
	@Operation(summary = "修改付款提醒备注")
	public R<String> setMark(@PathVariable Long id, @RequestBody Map<String, String> body) {
		if (ObjectUtil.isNull(id) || id <= 0) {
			return R.phpFailed("common.empty.attrs");
		}
		String mark = body == null ? "" : body.getOrDefault("mark", "");
		clientRemindCrmService.setMark(id, mark);
		return R.phpOk("common.operation.succ");
	}

	@PutMapping("/abjure/{id}")
	@Operation(summary = "放弃付款提醒")
	public R<String> abjure(@PathVariable Long id) {
		if (ObjectUtil.isNull(id) || id <= 0) {
			return R.phpFailed("common.empty.attrs");
		}
		clientRemindCrmService.abjure(id);
		return R.phpOk("common.operation.succ");
	}

	@GetMapping("/info/{id}")
	@Operation(summary = "获取付款提醒详情")
	public R<ClientRemind> info(@PathVariable Long id) {
		if (ObjectUtil.isNull(id) || id <= 0) {
			return R.phpFailed("common.empty.attrs");
		}
		ClientRemind row = clientRemindCrmService.getActiveById(id);
		if (row == null) {
			return R.phpFailed("数据获取异常");
		}
		return R.phpOk(row);
	}

}
