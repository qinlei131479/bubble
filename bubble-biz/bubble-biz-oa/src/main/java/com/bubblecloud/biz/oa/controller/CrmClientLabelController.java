package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.service.ClientLabelCrmService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.oa.api.entity.ClientLabel;
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
import cn.hutool.core.util.StrUtil;

/**
 * 客户标签（对齐 PHP {@code ent/client/labels}）。
 *
 * @author qinlei
 * @date 2026/4/3 14:00
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/client/labels")
@Tag(name = "CRM标签")
public class CrmClientLabelController {

	private final ClientLabelCrmService clientLabelCrmService;

	@GetMapping({"", "/"})
	@Operation(summary = "标签列表")
	public R<SimplePageVO> index(@ParameterObject Pg pg, @RequestParam(required = false) Long entid,
			@RequestParam(required = false) String name, @RequestParam(defaultValue = "0") Integer pid) {
		ClientLabel q = new ClientLabel();
		q.setEntid(ObjectUtil.defaultIfNull(entid, 1L));
		q.setPid(pid);
		if (StrUtil.isNotBlank(name)) {
			q.setName(name);
		}
		Page<ClientLabel> page = clientLabelCrmService.findPg(pg, q);
		return R.phpOk(SimplePageVO.of((int) page.getCurrent(), (int) page.getSize(), page.getTotal(), page.getRecords()));
	}

	@PostMapping({"", "/"})
	@Operation(summary = "保存标签")
	public R<String> store(@RequestBody ClientLabel body) {
		clientLabelCrmService.create(body);
		return R.phpOk(OaConstants.INSERT_SUCC);
	}

	@PutMapping("/{id}")
	@Operation(summary = "修改标签")
	public R<String> update(@PathVariable Long id, @RequestBody ClientLabel body) {
		body.setId(id);
		clientLabelCrmService.update(body);
		return R.phpOk(OaConstants.UPDATE_SUCC);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除标签")
	public R<String> destroy(@PathVariable Long id) {
		clientLabelCrmService.removeById(id);
		return R.phpOk(OaConstants.DELETE_SUCC);
	}

}
