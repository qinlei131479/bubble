package com.bubblecloud.biz.oa.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import com.bubblecloud.common.core.util.R;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.AdminMapper;
import com.bubblecloud.biz.oa.mapper.EnterpriseRoleMapper;
import com.bubblecloud.biz.oa.mapper.SystemMenusMapper;
import com.bubblecloud.biz.oa.service.MenusService;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.dto.MenusQueryDTO;
import com.bubblecloud.oa.api.entity.Admin;
import com.bubblecloud.oa.api.entity.EnterpriseRole;
import com.bubblecloud.oa.api.entity.SystemMenus;
import com.bubblecloud.oa.api.vo.MenuTreeNodeVO;
import com.bubblecloud.oa.api.vo.MenusVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 用户菜单与按钮权限实现。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@Service
@RequiredArgsConstructor
public class MenusServiceImpl extends UpServiceImpl<SystemMenusMapper, SystemMenus> implements MenusService {

	private final AdminMapper adminMapper;

	private final EnterpriseRoleMapper enterpriseRoleMapper;

	private final ObjectMapper objectMapper;

	@Override
	public MenusVO menus(MenusQueryDTO dto) {
		Admin admin = adminMapper
			.selectOne(Wrappers.lambdaQuery(Admin.class).eq(Admin::getId, dto.getUserId()).isNull(Admin::getDeletedAt));
		if (ObjectUtil.isNull(admin)) {
			return new MenusVO(Collections.emptyList(), Collections.emptyList());
		}

		boolean isAdmin = Objects.equals(admin.getIsAdmin(), 1);
		Set<Long> menuIds = isAdmin ? loadAllMenuIds() : loadMenuIdsFromUserRoles(admin.getRoles());

		List<SystemMenus> menuFlat = loadMenuListByIds(menuIds, "M");
		List<String> roleAuths = loadButtonAuthByIds(menuIds, "B");

		return new MenusVO(toMenuTreeVo(buildMenuTree(menuFlat)), roleAuths);
	}

	private Set<Long> loadAllMenuIds() {
		List<SystemMenus> rows = baseMapper.selectList(Wrappers.lambdaQuery(SystemMenus.class)
			.select(SystemMenus::getId)
			.eq(SystemMenus::getStatus, 1)
			.isNull(SystemMenus::getDeletedAt)
			.in(SystemMenus::getType, "M", "B", "A"));
		return rows.stream().map(SystemMenus::getId).collect(Collectors.toCollection(LinkedHashSet::new));
	}

	private Set<Long> loadMenuIdsFromUserRoles(String rawRoles) {
		List<Long> roleIds = parseIdList(rawRoles);
		if (roleIds.isEmpty()) {
			return Collections.emptySet();
		}
		List<EnterpriseRole> roles = enterpriseRoleMapper.selectList(Wrappers.lambdaQuery(EnterpriseRole.class)
			.eq(EnterpriseRole::getStatus, 1)
			.in(EnterpriseRole::getId, roleIds));
		Set<Long> menuIds = new LinkedHashSet<>();
		for (EnterpriseRole role : roles) {
			if (ObjectUtil.isNotNull(role.getRules())) {
				menuIds.addAll(parseIdList(role.getRules()));
			}
		}
		return menuIds;
	}

	private List<SystemMenus> loadMenuListByIds(Set<Long> menuIds, String type) {
		if (menuIds.isEmpty()) {
			return Collections.emptyList();
		}
		return baseMapper.selectList(Wrappers.lambdaQuery(SystemMenus.class)
			.eq(SystemMenus::getStatus, 1)
			.isNull(SystemMenus::getDeletedAt)
			.eq(SystemMenus::getType, type)
			.in(SystemMenus::getId, menuIds)
			.orderByDesc(SystemMenus::getSort)
			.orderByAsc(SystemMenus::getId));
	}

	private List<String> loadButtonAuthByIds(Set<Long> menuIds, String type) {
		if (menuIds.isEmpty()) {
			return Collections.emptyList();
		}
		List<SystemMenus> rows = baseMapper.selectList(Wrappers.lambdaQuery(SystemMenus.class)
			.select(SystemMenus::getUniqueAuth)
			.eq(SystemMenus::getStatus, 1)
			.isNull(SystemMenus::getDeletedAt)
			.eq(SystemMenus::getType, type)
			.in(SystemMenus::getId, menuIds));
		return rows.stream().map(SystemMenus::getUniqueAuth).filter(StrUtil::isNotBlank).distinct().toList();
	}

	private List<MenuTreeNodeVO> toMenuTreeVo(List<SystemMenus> tree) {
		if (ObjectUtil.isNull(tree) || tree.isEmpty()) {
			return Collections.emptyList();
		}
		List<MenuTreeNodeVO> list = new ArrayList<>(tree.size());
		for (SystemMenus node : tree) {
			list.add(toMenuNodeVo(node));
		}
		return list;
	}

	private MenuTreeNodeVO toMenuNodeVo(SystemMenus m) {
		MenuTreeNodeVO vo = new MenuTreeNodeVO();
		vo.setId(m.getId());
		vo.setPid(m.getPid());
		vo.setMenuName(m.getMenuName());
		vo.setMenuPath(m.getMenuPath());
		vo.setUniPath(blankToEmpty(m.getUniPath()));
		vo.setUniImg(blankToEmpty(m.getUniImg()));
		vo.setIcon(blankToEmpty(m.getIcon()));
		vo.setPath(m.getPath());
		vo.setEntid(m.getEntid());
		vo.setPosition(m.getPosition());
		vo.setUniqueAuth(m.getUniqueAuth());
		vo.setIsShow(m.getIsShow());
		vo.setComponent(blankToEmpty(m.getComponent()));
		List<MenuTreeNodeVO> children = new ArrayList<>();
		if (ObjectUtil.isNotNull(m.getTopPosition())) {
			for (SystemMenus c : m.getTopPosition()) {
				children.add(toMenuNodeVo(c));
			}
		}
		if (ObjectUtil.isNotNull(m.getChildren())) {
			for (SystemMenus c : m.getChildren()) {
				children.add(toMenuNodeVo(c));
			}
		}
		if (!children.isEmpty()) {
			vo.setChildren(children);
		}
		return vo;
	}

	private static String blankToEmpty(String s) {
		return StrUtil.isNotBlank(s) ? s : "";
	}

	private List<SystemMenus> buildMenuTree(List<SystemMenus> menuFlat) {
		Map<Long, SystemMenus> nodeMap = new LinkedHashMap<>();
		for (SystemMenus row : menuFlat) {
			nodeMap.put(row.getId(), row);
		}
		List<SystemMenus> tree = new ArrayList<>();
		for (SystemMenus node : nodeMap.values()) {
			Long pid = node.getPid();
			Long pidLong = ObjectUtil.isNull(pid) ? null : pid.longValue();
			if (ObjectUtil.isNotNull(pidLong) && nodeMap.containsKey(pidLong)) {
				SystemMenus parent = nodeMap.get(pidLong);
				if (Integer.valueOf(1).equals(node.getPosition())) {
					if (ObjectUtil.isNull(parent.getTopPosition())) {
						parent.setTopPosition(new ArrayList<>());
					}
					parent.getTopPosition().add(node);
				}
				else {
					if (ObjectUtil.isNull(parent.getChildren())) {
						parent.setChildren(new ArrayList<>());
					}
					parent.getChildren().add(node);
				}
			}
			else {
				tree.add(node);
			}
		}
		return tree;
	}

	private List<Long> parseIdList(String raw) {
		if (StrUtil.isBlank(raw)) {
			return Collections.emptyList();
		}
		String value = raw.trim();
		try {
			if (value.startsWith("[") && value.endsWith("]")) {
				List<Long> parsed = objectMapper.readValue(value, new TypeReference<List<Long>>() {
				});
				return parsed.stream().filter(Objects::nonNull).toList();
			}
		}
		catch (Exception ignored) {
			// fallback
		}
		String normalized = value.replace("[", "").replace("]", "").replace("\"", "");
		if (StrUtil.isBlank(normalized)) {
			return Collections.emptyList();
		}
		return List.of(normalized.split(",")).stream().map(String::trim).filter(StrUtil::isNotBlank).map(item -> {
			try {
				return Long.parseLong(item);
			}
			catch (NumberFormatException ex) {
				return null;
			}
		}).filter(Objects::nonNull).toList();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(SystemMenus req) {
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(SystemMenus req) {
		return super.update(req);
	}

}
