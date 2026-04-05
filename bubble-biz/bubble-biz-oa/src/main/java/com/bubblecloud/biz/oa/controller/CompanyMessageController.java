package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.biz.oa.service.AdminService;
import com.bubblecloud.biz.oa.service.EnterpriseMessageNoticeService;
import com.bubblecloud.biz.oa.util.OaSecurityUtil;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.dto.CompanyMessageBatchDTO;
import com.bubblecloud.oa.api.entity.Admin;
import com.bubblecloud.oa.api.vo.common.CommonMessageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 企业消息（对齐 PHP {@code ent/company/message}，含消息中心抽屉）。
 *
 * @author qinlei
 * @date 2026/4/5
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/company/message")
@Tag(name = "企业消息")
public class CompanyMessageController {

	private final EnterpriseMessageNoticeService enterpriseMessageNoticeService;

	private final AdminService adminService;

	@GetMapping({ "", "/" })
	@Operation(summary = "当前用户消息列表（抽屉 noticeMessageListApi）")
	public R<CommonMessageVO> index(@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "15") Integer limit, @RequestParam(required = false) String cate_id,
			@RequestParam(required = false) String title, @RequestParam(required = false) String is_read,
			@RequestParam(defaultValue = "1") Long entid) {
		Long userId = OaSecurityUtil.currentUserId();
		if (ObjectUtil.isNull(userId)) {
			return R.phpFailed("未登录");
		}
		Admin admin = adminService.getById(userId);
		if (ObjectUtil.isNull(admin)) {
			return R.phpFailed("用户不存在");
		}
		return R.phpOk(enterpriseMessageNoticeService.getCompanyMessage(userId, StrUtil.nullToEmpty(admin.getUid()),
				entid, page, limit, StrUtil.nullToEmpty(cate_id), StrUtil.nullToEmpty(title), is_read));
	}

	@GetMapping("/list")
	@Operation(summary = "企业全部消息列表（不按接收人过滤，对齐 PHP list）")
	public R<CommonMessageVO> listAll(@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "20") Integer limit, @RequestParam(required = false) String cate_id,
			@RequestParam(required = false) String title, @RequestParam(required = false) String is_read,
			@RequestParam(defaultValue = "1") Long entid) {
		return R.phpOk(enterpriseMessageNoticeService.getCompanyMessageAll(entid, page, limit,
				StrUtil.nullToEmpty(cate_id), StrUtil.nullToEmpty(title), is_read));
	}

	@PutMapping("/batch/{isRead}")
	@Operation(summary = "批量已读/未读（对齐 PHP batch/{isRead}）")
	public R<String> batchRead(@PathVariable int isRead, @RequestBody(required = false) CompanyMessageBatchDTO body,
			@RequestParam(defaultValue = "1") Long entid) {
		Long userId = OaSecurityUtil.currentUserId();
		if (ObjectUtil.isNull(userId)) {
			return R.phpFailed("未登录");
		}
		Admin admin = adminService.getById(userId);
		if (ObjectUtil.isNull(admin)) {
			return R.phpFailed("用户不存在");
		}
		Long cateId = body != null ? body.getCateId() : null;
		var ids = body != null && body.getIds() != null ? body.getIds() : java.util.Collections.<Long>emptyList();
		try {
			enterpriseMessageNoticeService.batchUpdateCompanyMessageRead(userId, StrUtil.nullToEmpty(admin.getUid()),
					entid, isRead, cateId, ids);
			return R.phpOk("修改成功");
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(StrUtil.blankToDefault(e.getMessage(), "修改失败"));
		}
	}

	@DeleteMapping("/batch")
	@Operation(summary = "批量删除（对齐 PHP DELETE batch）")
	public R<String> batchDelete(@RequestBody(required = false) CompanyMessageBatchDTO body,
			@RequestParam(defaultValue = "1") Long entid) {
		Long userId = OaSecurityUtil.currentUserId();
		if (ObjectUtil.isNull(userId)) {
			return R.phpFailed("未登录");
		}
		Admin admin = adminService.getById(userId);
		if (ObjectUtil.isNull(admin)) {
			return R.phpFailed("用户不存在");
		}
		var ids = body != null && body.getIds() != null ? body.getIds() : java.util.Collections.<Long>emptyList();
		try {
			enterpriseMessageNoticeService.batchDeleteCompanyMessages(userId, StrUtil.nullToEmpty(admin.getUid()),
					entid, ids);
			return R.phpOk("删除成功");
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(StrUtil.blankToDefault(e.getMessage(), "删除失败"));
		}
	}

}
