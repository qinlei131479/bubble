package com.bubblecloud.biz.oa.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.bubblecloud.common.core.util.R;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.SystemMenusMapper;
import com.bubblecloud.biz.oa.service.SystemMenusService;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.biz.oa.util.TreeUtil;
import com.bubblecloud.oa.api.entity.SystemMenus;
import com.bubblecloud.oa.api.vo.menu.SystemMenusTreeNodeVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import cn.hutool.core.util.StrUtil;

/**
 * 企业菜单管理实现。
 *
 * @author qinlei
 * @date 2026/3/30
 */
@Service
@RequiredArgsConstructor
public class SystemMenusServiceImpl extends UpServiceImpl<SystemMenusMapper, SystemMenus>
		implements SystemMenusService {

	private final ObjectMapper objectMapper;

	@Override
	public List<SystemMenusTreeNodeVO> listMenuTree(String menuName, Long entId) {
		var q = Wrappers.lambdaQuery(SystemMenus.class)
			.eq(SystemMenus::getEntid, entId)
			.isNull(SystemMenus::getDeletedAt);
		if (StrUtil.isNotBlank(menuName)) {
			q.like(SystemMenus::getMenuName, menuName);
		}
		q.orderByDesc(SystemMenus::getSort).orderByAsc(SystemMenus::getId);
		List<SystemMenus> rows = baseMapper.selectList(q);
		List<SystemMenusTreeNodeVO> flat = new ArrayList<>(rows.size());
		for (SystemMenus m : rows) {
			SystemMenusTreeNodeVO n = new SystemMenusTreeNodeVO();
			n.setId(m.getId());
			n.setPid(m.getPid());
			n.setMenuName(m.getMenuName());
			n.setIsShow(m.getIsShow());
			n.setType(m.getType());
			n.setSort(m.getSort());
			flat.add(n);
		}
		return TreeUtil.buildTree(flat, SystemMenusTreeNodeVO::getId, SystemMenusTreeNodeVO::getPid,
				SystemMenusTreeNodeVO::getChildren);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R deleteById(Long id) {
		long child = baseMapper.selectCount(Wrappers.lambdaQuery(SystemMenus.class).eq(SystemMenus::getPid, id));
		if (child > 0) {
			throw new IllegalArgumentException("请先删除下级菜单");
		}
		return super.deleteById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateIsShow(Long id, Integer isShow) {
		SystemMenus m = new SystemMenus();
		m.setId(id);
		m.setIsShow(isShow);
		m.setUpdatedAt(LocalDateTime.now());
		baseMapper.updateById(m);
	}

	@Override
	public JsonNode getNotSaveMenus(Long entId) {
		ObjectNode root = objectMapper.createObjectNode();
		root.set("ent", objectMapper.createArrayNode());
		root.set("uni", objectMapper.createArrayNode());
		return root;
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
