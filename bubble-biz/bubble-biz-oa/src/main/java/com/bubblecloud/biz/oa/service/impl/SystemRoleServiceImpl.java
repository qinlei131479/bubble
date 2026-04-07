package com.bubblecloud.biz.oa.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.SystemRoleMapper;
import com.bubblecloud.biz.oa.service.SystemRoleService;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.SystemRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;

/**
 * eb_system_role 读写实现。
 *
 * @author qinlei
 * @date 2026/4/6 15:30
 */
@Service
@RequiredArgsConstructor
public class SystemRoleServiceImpl extends UpServiceImpl<SystemRoleMapper, SystemRole> implements SystemRoleService {

	private static final String TYPE_ENTERPRISE = "enterprise";

	private final ObjectMapper objectMapper;

	@Override
	public SystemRole getDefaultEnterpriseTemplate() {
		return baseMapper.selectOne(Wrappers.lambdaQuery(SystemRole.class)
			.eq(SystemRole::getEntid, 0L)
			.and(w -> w.eq(SystemRole::getType, "0")
				.or()
				.eq(SystemRole::getType, "")
				.or()
				.isNull(SystemRole::getType))
			.orderByAsc(SystemRole::getId)
			.last("LIMIT 1"));
	}

	@Override
	public SystemRole getEnterpriseSuperRole(Long entId) {
		return baseMapper.selectOne(Wrappers.lambdaQuery(SystemRole.class)
			.eq(SystemRole::getEntid, entId)
			.eq(SystemRole::getType, TYPE_ENTERPRISE)
			.eq(SystemRole::getLevel, 0)
			.last("LIMIT 1"));
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveEnterpriseSuperRole(Long entId, List<Long> rules, List<Long> apis) {
		try {
			String rulesJson = objectMapper.writeValueAsString(CollUtil.emptyIfNull(rules));
			String apisJson = objectMapper.writeValueAsString(CollUtil.emptyIfNull(apis));
			SystemRole row = getEnterpriseSuperRole(entId);
			if (ObjectUtil.isNull(row)) {
				row = new SystemRole();
				row.setEntid(entId);
				row.setType(TYPE_ENTERPRISE);
				row.setLevel(0);
				row.setRoleName("企业权限");
				row.setStatus(1);
				row.setRules(rulesJson);
				row.setApis(apisJson);
				baseMapper.insert(row);
			}
			else {
				row.setRules(rulesJson);
				row.setApis(apisJson);
				baseMapper.updateById(row);
			}
		}
		catch (Exception e) {
			throw new IllegalArgumentException("保存企业超级角色失败: " + e.getMessage());
		}
	}

}
