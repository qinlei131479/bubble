package com.bubblecloud.biz.oa.controller;

import java.util.List;

import com.bubblecloud.biz.oa.service.AdminService;
import com.bubblecloud.biz.oa.service.MessageService;
import com.bubblecloud.biz.oa.util.OaSecurityUtil;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.entity.Admin;
import com.bubblecloud.oa.api.entity.Message;
import com.bubblecloud.oa.api.vo.message.MessageCategoryCountVO;
import com.bubblecloud.oa.api.vo.message.SystemMessageListResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

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
public class MessageController {

	private final MessageService messageService;

	private final AdminService adminService;

	@GetMapping("/list")
	@Operation(summary = "消息列表（data.list + data.count，含 message_template）")
	public R<SystemMessageListResultVO> list(@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "20") Integer limit, @RequestParam(defaultValue = "0") Long cate_id,
			@RequestParam(required = false) String title, @RequestParam(defaultValue = "1") Long entid) {
		return R.phpOk(messageService.getSystemMessageList(entid, page, limit, cate_id, StrUtil.nullToEmpty(title)));
	}

	/** 兼容旧前端 {@code /page} 路径 */
	@GetMapping("/page")
	@Operation(summary = "消息列表（别名）")
	public R<SystemMessageListResultVO> pageAlias(@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "20") Integer limit, @RequestParam(defaultValue = "0") Long cate_id,
			@RequestParam(required = false) String title, @RequestParam(defaultValue = "1") Long entid) {
		return list(page, limit, cate_id, title, entid);
	}

	@GetMapping("/cate")
	@Operation(summary = "消息分类及未读条数（对齐 PHP message/cate）")
	public R<List<MessageCategoryCountVO>> cate(@RequestParam(defaultValue = "1") Long entid) {
		Long adminId = OaSecurityUtil.currentUserId();
		if (ObjectUtil.isNull(adminId)) {
			return R.phpFailed("未登录");
		}
		Admin admin = adminService.getById(adminId);
		String uuid = ObjectUtil.isNull(admin) ? "" : StrUtil.nullToEmpty(admin.getUid());
		return R.phpOk(messageService.getMessageCateCount(entid, adminId, uuid));
	}

	@GetMapping("/find/{id}")
	@Operation(summary = "消息详情")
	public R<Message> details(@PathVariable Long id) {
		return R.phpOk(messageService.getById(id));
	}

}
