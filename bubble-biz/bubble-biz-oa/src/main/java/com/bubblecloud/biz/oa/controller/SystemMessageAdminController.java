package com.bubblecloud.biz.oa.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.service.SystemMessageAdminService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.oa.api.entity.Message;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统消息配置（对齐 PHP {@code ent/system/message}）。
 *
 * @author qinlei
 * @date 2026/3/30
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/system/message")
@Tag(name = "系统消息配置")
public class SystemMessageAdminController {

	private final SystemMessageAdminService systemMessageAdminService;

	@GetMapping(value = { "/list", "/page" })
	@Operation(summary = "消息列表")
	public R<Page<Message>> page(@RequestParam(required = false) Integer cate_id,
			@RequestParam(required = false) String title, @RequestParam(defaultValue = "1") Integer entid,
			@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "20") Integer limit) {
		Pg<Message> pg = new Pg<>();
		pg.setCurrent(pageNum);
		pg.setSize(limit);
		Message query = new Message();
		query.setEntid(entid);
		query.setCateId(cate_id);
		query.setTitle(title);
		return R.phpOk(systemMessageAdminService.findPg(pg, query));
	}

	@GetMapping("/find/{id}")
	@Operation(summary = "消息详情")
	public R<Message> details(@PathVariable Long id) {
		return R.phpOk(systemMessageAdminService.getById(id));
	}

}
