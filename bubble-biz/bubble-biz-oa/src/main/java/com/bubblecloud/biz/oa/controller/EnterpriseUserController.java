package com.bubblecloud.biz.oa.controller;

import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.util.OaSecurityUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bubblecloud.oa.api.dto.EnterpriseUserCardUpdateDTO;
import jakarta.validation.Valid;

import com.bubblecloud.biz.oa.service.EnterpriseUserService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.vo.SimplePageVO;
import com.bubblecloud.oa.api.vo.enterprise.EnterpriseUserCardVO;
import com.bubblecloud.oa.api.vo.enterprise.EnterpriseUserProfileVO;
import com.bubblecloud.oa.api.vo.enterprise.UserFrameBriefVO;
import com.bubblecloud.oa.api.vo.frame.FrameDepartmentTreeNodeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * 企业用户（对齐 PHP {@code EnterpriseUserController}，前缀 {@code ent/user}）。
 *
 * @author qinlei
 * @date 2026/3/29 下午5:30
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/user")
@Tag(name = "企业用户")
public class EnterpriseUserController {

	private final EnterpriseUserService enterpriseUserService;

	@GetMapping("/list")
	@Operation(summary = "组织架构人员列表")
	public R<SimplePageVO> page(@RequestParam(defaultValue = "1") Integer entid, @RequestParam(required = false) String pid,
								@RequestParam(required = false) String name, @RequestParam(defaultValue = "1") Integer status,
								@RequestParam(defaultValue = "1") Integer current, @RequestParam(defaultValue = "20") Integer size) {
		return R.phpOk(enterpriseUserService.listEnterpriseUsers(entid, pid, name, status, current, size));
	}

	@GetMapping("/card/{id}")
	@Operation(summary = "组织架构成员信息")
	public R<EnterpriseUserCardVO> editUser(@PathVariable Long id, @RequestParam(defaultValue = "1") Integer entid) {
		return R.phpOk(enterpriseUserService.getCardEdit(id, entid));
	}

	@PutMapping("/card/{id}")
	@Operation(summary = "修改组织架构成员")
	public R<String> updateUser(@PathVariable Long id, @RequestParam(defaultValue = "1") Integer entid,
								@RequestBody @Valid EnterpriseUserCardUpdateDTO dto) {
		enterpriseUserService.updateEnterpriseUserCard(id, entid, dto);
		return R.phpOk(OaConstants.UPDATE_SUCC);
	}

	@GetMapping("/userInfo")
	@Operation(summary = "获取用户关联企业详情")
	public R<EnterpriseUserProfileVO> userInfo(@RequestParam(defaultValue = "1") Integer entid) {
		return R.phpOk(enterpriseUserService.userInfo(OaSecurityUtil.currentUserId(), entid));
	}

	@GetMapping("/userFrame")
	@Operation(summary = "获取用户部门信息")
	public R<UserFrameBriefVO> userFrame(@RequestParam(defaultValue = "1") Integer entid) {
		return R.phpOk(enterpriseUserService.userFrame(OaSecurityUtil.currentUserId(), entid));
	}

	@GetMapping("/add_book/tree")
	@Operation(summary = "通讯录部门树")
	public R<List<FrameDepartmentTreeNodeVO>> getFrameTree(@RequestParam(defaultValue = "1") Integer entid,
														   @RequestParam(required = false) String name) {
		return R.phpOk(enterpriseUserService.addressBookTree(entid, name));
	}

	@GetMapping("/add_book/list")
	@Operation(summary = "通讯录用户列表")
	public R<SimplePageVO> addressBook(@RequestParam(defaultValue = "1") Integer entid,
									   @RequestParam(required = false) String name, @RequestParam(required = false) Integer status,
									   @RequestParam(defaultValue = "1") Integer current, @RequestParam(defaultValue = "20") Integer size) {
		return R.phpOk(enterpriseUserService.addressBook(entid, name, status, current, size));
	}

}
