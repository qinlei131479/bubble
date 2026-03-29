package com.bubblecloud.biz.oa.controller;

import java.util.List;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.EnterpriseRoleMapper;
import com.bubblecloud.biz.oa.support.PhpResponse;
import com.bubblecloud.oa.api.entity.EnterpriseRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 企业角色列表（对齐 PHP {@code ent/system/roles} index）。
 *
 * @author qinlei
 * @date 2026/3/29 下午12:00
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/system/roles")
@Tag(name = "企业角色")
public class RolesAdminController {

	private final EnterpriseRoleMapper enterpriseRoleMapper;

	@GetMapping
	@Operation(summary = "角色列表")
	public PhpResponse<List<EnterpriseRole>> index(@RequestParam(required = false) String role_name,
												   @RequestParam(defaultValue = "1") int entid) {
		var q = Wrappers.lambdaQuery(EnterpriseRole.class).eq(EnterpriseRole::getEntid, entid);
		if (StringUtils.hasText(role_name)) {
			q.like(EnterpriseRole::getRoleName, role_name);
		}
		q.orderByDesc(EnterpriseRole::getId);
		return PhpResponse.ok(enterpriseRoleMapper.selectList(q));
	}

}
