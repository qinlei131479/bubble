package com.bubblecloud.biz.oa.controller;

import java.util.ArrayList;
import java.util.List;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.SystemMenusMapper;
import com.bubblecloud.biz.oa.support.PhpResponse;
import com.bubblecloud.biz.oa.util.TreeUtil;
import com.bubblecloud.oa.api.entity.SystemMenus;
import com.bubblecloud.oa.api.vo.menu.MenuAdminTreeNodeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 企业菜单管理（对齐 PHP {@code ent/system/menus} 列表）。
 *
 * @author qinlei
 * @date 2026/3/29 下午4:00
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ent/system/menus")
@Tag(name = "企业菜单管理")
public class MenusAdminController {

	private final SystemMenusMapper systemMenusMapper;

	@GetMapping
	@Operation(summary = "企业菜单树")
	public PhpResponse<List<MenuAdminTreeNodeVO>> index(@RequestParam(required = false) String menu_name,
														@RequestParam(defaultValue = "1") int entid) {
		var q = Wrappers.lambdaQuery(SystemMenus.class)
				.eq(SystemMenus::getEntid, entid)
				.isNull(SystemMenus::getDeletedAt);
		if (StringUtils.hasText(menu_name)) {
			q.like(SystemMenus::getMenuName, menu_name);
		}
		q.orderByDesc(SystemMenus::getSort).orderByAsc(SystemMenus::getId);
		List<SystemMenus> rows = systemMenusMapper.selectList(q);
		List<MenuAdminTreeNodeVO> flat = new ArrayList<>(rows.size());
		for (SystemMenus m : rows) {
			MenuAdminTreeNodeVO n = new MenuAdminTreeNodeVO();
			n.setId(m.getId());
			n.setPid(m.getPid());
			n.setMenuName(m.getMenuName());
			n.setIsShow(m.getIsShow());
			n.setType(m.getType());
			n.setSort(m.getSort());
			flat.add(n);
		}
		return PhpResponse.ok(TreeUtil.buildTree(flat, MenuAdminTreeNodeVO::getId, MenuAdminTreeNodeVO::getPid,
				MenuAdminTreeNodeVO::getChildren));
	}

}
