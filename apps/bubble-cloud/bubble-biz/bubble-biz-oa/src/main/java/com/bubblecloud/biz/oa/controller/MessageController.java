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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

	private final ObjectMapper objectMapper;

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

	@PutMapping("/update/{id}")
	@Operation(summary = "修改消息（提醒时间 / 渠道模板）")
	public R<String> update(@PathVariable Long id, @RequestBody(required = false) JsonNode body) {
		try {
			JsonNode payload = body == null ? objectMapper.createObjectNode() : body;
			messageService.updateMessage(id, payload);
			return R.phpOk("修改成功");
		}
		catch (IllegalArgumentException ex) {
			return R.phpFailed(ex.getMessage());
		}
	}

	@PutMapping("/status/{id}/{type}")
	@Operation(summary = "修改某渠道模板状态")
	public R<String> status(@PathVariable Long id, @PathVariable Integer type,
			@RequestBody(required = false) JsonNode body) {
		int st = body == null ? 0 : body.path("status").asInt(0);
		try {
			messageService.updateChannelStatus(id, type, st);
			return R.phpOk("修改成功");
		}
		catch (IllegalArgumentException ex) {
			return R.phpFailed(ex.getMessage());
		}
	}

	@PutMapping("/subscribe/{id}")
	@Operation(summary = "用户是否可取消订阅")
	public R<String> subscribe(@PathVariable Long id, @RequestBody(required = false) JsonNode body) {
		int st = body == null ? 0 : body.path("status").asInt(0);
		try {
			messageService.setUserSubscribe(id, st);
			return R.phpOk("修改成功");
		}
		catch (IllegalArgumentException ex) {
			return R.phpFailed(ex.getMessage());
		}
	}

	@PutMapping("/sync")
	@Operation(summary = "同步系统消息（占位：未对接远程中心）")
	public R<String> sync(@RequestParam(defaultValue = "1") Long entid) {
		messageService.syncMessage(entid);
		return R.phpOk("ok");
	}

}
