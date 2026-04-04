package com.bubblecloud.biz.oa.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.CategoryMapper;
import com.bubblecloud.biz.oa.mapper.SystemAttachMapper;
import com.bubblecloud.biz.oa.service.AttachCateAdminService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.Category;
import com.bubblecloud.oa.api.entity.SystemAttach;
import com.bubblecloud.oa.api.vo.CategoryAttachTreeVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.util.ObjectUtil;

/**
 * 附件分类实现。
 *
 * @author qinlei
 * @date 2026/3/31
 */
@Service
public class AttachCateAdminServiceImpl extends UpServiceImpl<CategoryMapper, Category>
		implements AttachCateAdminService {

	private static final String TYPE_ATTACH = "systemAttach";

	private final SystemAttachMapper systemAttachMapper;

	public AttachCateAdminServiceImpl(SystemAttachMapper systemAttachMapper) {
		this.systemAttachMapper = systemAttachMapper;
	}

	@Override
	public List<CategoryAttachTreeVO> attachCateTree(int entid) {
		List<Category> flat = list(Wrappers.lambdaQuery(Category.class)
			.eq(Category::getType, TYPE_ATTACH)
			.eq(Category::getEntid, entid)
			.orderByAsc(Category::getSort)
			.orderByDesc(Category::getId));
		Map<Long, CategoryAttachTreeVO> map = new HashMap<>();
		for (Category c : flat) {
			map.put(c.getId(), toNode(c));
		}
		List<CategoryAttachTreeVO> roots = new ArrayList<>();
		for (Category c : flat) {
			CategoryAttachTreeVO node = map.get(c.getId());
			int pid = ObjectUtil.defaultIfNull(c.getPid(), 0);
			if (pid <= 0) {
				roots.add(node);
			}
			else {
				CategoryAttachTreeVO parent = map.get((long) pid);
				if (ObjectUtil.isNotNull(parent)) {
					parent.getChildren().add(node);
				}
				else {
					roots.add(node);
				}
			}
		}
		return roots;
	}

	private static CategoryAttachTreeVO toNode(Category c) {
		CategoryAttachTreeVO v = new CategoryAttachTreeVO();
		v.setId(c.getId());
		v.setPid(c.getPid());
		v.setCateName(c.getCateName());
		v.setPath(c.getPath());
		v.setSort(c.getSort());
		v.setPic(c.getPic());
		v.setIsShow(c.getIsShow());
		v.setLevel(c.getLevel());
		v.setType(c.getType());
		v.setKeyword(c.getKeyword());
		v.setEntid(c.getEntid());
		v.setChildren(new ArrayList<>());
		return v;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteAttachCategory(Long id, int entid) {
		if (ObjectUtil.isNull(id)) {
			throw new IllegalArgumentException("参数错误");
		}
		Category row = getById(id);
		if (ObjectUtil.isNull(row) || row.getEntid() == null || row.getEntid() != entid) {
			throw new IllegalArgumentException("没有查询到数据！");
		}
		long cnt = systemAttachMapper.selectCount(Wrappers.lambdaQuery(SystemAttach.class)
			.eq(SystemAttach::getCid, id.intValue())
			.eq(SystemAttach::getEntid, entid));
		if (cnt > 0) {
			throw new IllegalArgumentException("删除失败，该分类下有内容！");
		}
		removeById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(Category req) {
		req.setType(TYPE_ATTACH);
		if (ObjectUtil.isNull(req.getEntid())) {
			req.setEntid(1);
		}
		if (ObjectUtil.isNull(req.getPid())) {
			req.setPid(0);
		}
		if (ObjectUtil.isNull(req.getSort())) {
			req.setSort(0);
		}
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(Category req) {
		req.setType(TYPE_ATTACH);
		return super.update(req);
	}

}
