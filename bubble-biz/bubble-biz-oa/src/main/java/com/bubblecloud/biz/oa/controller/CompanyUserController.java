package com.bubblecloud.biz.oa.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bubblecloud.oa.api.dto.EnterpriseUserCardUpdateDTO;
import jakarta.validation.Valid;

import com.bubblecloud.biz.oa.security.OaCurrentUser;
import com.bubblecloud.biz.oa.service.CompanyUserService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.oa.api.vo.SimplePageVO;
import com.bubblecloud.oa.api.vo.company.CompanyUserCardVO;
import com.bubblecloud.oa.api.vo.company.CompanyUserProfileVO;
import com.bubblecloud.oa.api.vo.company.UserFrameBriefVO;
import com.bubblecloud.oa.api.vo.frame.FrameDepartmentTreeNodeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.util.List;
import cn.hutool.core.util.ObjectUtil;

/**
 * 企业用户（对齐 PHP {@code CompanyUserController}，前缀 {@code ent/user}）。
 *
 * @author qinlei
 * @date 2026/3/29 下午5:30
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/user")
@Tag(name = "企业用户")
public class CompanyUserController {

	private final CompanyUserService companyUserService;

	@GetMapping("/list")
	@Operation(summary = "组织架构人员列表")
	public R<SimplePageVO> page(@RequestParam(defaultValue = "1") int entid,
			@RequestParam(required = false) String pid, @RequestParam(required = false) String name,
			@RequestParam(defaultValue = "1") Integer status, @RequestParam(defaultValue = "1") int current,
			@RequestParam(defaultValue = "20") int size) {
		return R.phpOk(companyUserService.listCompanyUsers(entid, pid, name, status, current, size));
	}

	@GetMapping("/card/{id}")
	@Operation(summary = "组织架构成员信息")
	public R<CompanyUserCardVO> editUser(@PathVariable long id, @RequestParam(defaultValue = "1") int entid) {
		try {
			return R.phpOk(companyUserService.getCardEdit(id, entid));
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@PutMapping("/card/{id}")
	@Operation(summary = "修改组织架构成员")
	public R<String> updateUser(@PathVariable long id, @RequestParam(defaultValue = "1") int entid,
			@RequestBody @Valid EnterpriseUserCardUpdateDTO dto) {
		try {
			companyUserService.updateCompanyUserCard(id, entid, dto);
			return R.phpOk("common.update.succ");
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@GetMapping("/userInfo")
	@Operation(summary = "获取用户关联企业详情")
	public R<CompanyUserProfileVO> userInfo(Authentication authentication,
			@RequestParam(defaultValue = "1") int entid) {
		if (ObjectUtil.isNull(authentication) || !(authentication.getPrincipal() instanceof OaCurrentUser u)) {
			return R.phpFailed("未登录");
		}
		try {
			return R.phpOk(companyUserService.userInfo(u.getId(), entid));
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@GetMapping("/userFrame")
	@Operation(summary = "获取用户部门信息")
	public R<UserFrameBriefVO> userFrame(Authentication authentication,
			@RequestParam(defaultValue = "1") int entid) {
		if (ObjectUtil.isNull(authentication) || !(authentication.getPrincipal() instanceof OaCurrentUser u)) {
			return R.phpFailed("未登录");
		}
		try {
			return R.phpOk(companyUserService.userFrame(u.getId(), entid));
		}
		catch (IllegalArgumentException e) {
			return R.phpFailed(e.getMessage());
		}
	}

	@GetMapping("/add_book/tree")
	@Operation(summary = "通讯录部门树")
	public R<List<FrameDepartmentTreeNodeVO>> getFrameTree(@RequestParam(defaultValue = "1") int entid,
			@RequestParam(required = false) String name) {
		return R.phpOk(companyUserService.addressBookTree(entid, name));
	}

	@GetMapping("/add_book/list")
	@Operation(summary = "通讯录用户列表")
	public R<SimplePageVO> addressBook(@RequestParam(defaultValue = "1") int entid,
			@RequestParam(required = false) String name, @RequestParam(required = false) Integer status,
			@RequestParam(defaultValue = "1") int current, @RequestParam(defaultValue = "20") int size) {
		return R.phpOk(companyUserService.addressBook(entid, name, status, current, size));
	}

}
