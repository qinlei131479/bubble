package com.bubblecloud.biz.oa.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.mapper.BillCategoryMapper;
import com.bubblecloud.biz.oa.mapper.BillListMapper;
import com.bubblecloud.biz.oa.service.BillCategoryService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.BillCategory;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 财务分类实现。
 *
 * @author qinlei
 * @date 2026/4/6 16:00
 */
@Service
public class BillCategoryServiceImpl extends UpServiceImpl<BillCategoryMapper, BillCategory>
		implements BillCategoryService {

	@Autowired
	private BillListMapper billListMapper;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(BillCategory dto) {
		normalizePath(dto);
		long dup = count(Wrappers.lambdaQuery(BillCategory.class)
			.eq(BillCategory::getPid, dto.getPid())
			.eq(BillCategory::getEntid, dto.getEntid())
			.eq(BillCategory::getName, dto.getName()));
		if (dup > 0) {
			throw new IllegalArgumentException("分类已存在，请勿重复添加");
		}
		dto.setCateNo(generateNo(ObjectUtil.defaultIfNull(dto.getPid(), 0), dto.getEntid()));
		dto.setLevel(calcLevel(dto.getPath()));
		dto.setSort(ObjectUtil.defaultIfNull(dto.getSort(), 0));
		dto.setContactId(ObjectUtil.defaultIfNull(dto.getContactId(), 0));
		return super.create(dto);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(BillCategory dto) {
		BillCategory existing = baseMapper.selectById(dto.getId());
		if (ObjectUtil.isNull(existing)) {
			throw new IllegalArgumentException(OaConstants.NOT_EXISTS);
		}
		normalizePath(dto);
		long dup = count(Wrappers.lambdaQuery(BillCategory.class)
			.ne(BillCategory::getId, dto.getId())
			.eq(BillCategory::getPid, dto.getPid())
			.eq(BillCategory::getEntid, dto.getEntid())
			.eq(BillCategory::getName, dto.getName())
			.eq(BillCategory::getTypes, existing.getTypes()));
		if (dup > 0) {
			throw new IllegalArgumentException("分类已存在，请勿重复添加");
		}
		if (ObjectUtil.equal(dto.getPid(), dto.getId().intValue())) {
			throw new IllegalArgumentException("前置分类不能为自己");
		}
		dto.setLevel(calcLevel(dto.getPath()));
		if (StrUtil.isBlank(existing.getCateNo()) || !StrUtil.equals(dto.getPath(), existing.getPath())) {
			dto.setCateNo(generateNo(ObjectUtil.defaultIfNull(dto.getPid(), 0), dto.getEntid()));
		}
		else {
			dto.setCateNo(existing.getCateNo());
		}
		dto.setTypes(existing.getTypes());
		return super.update(dto);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean removeById(java.io.Serializable id) {
		long used = billListMapper.selectCount(Wrappers.lambdaQuery(com.bubblecloud.oa.api.entity.BillList.class)
			.eq(com.bubblecloud.oa.api.entity.BillList::getCateId, id));
		if (used > 0) {
			throw new IllegalArgumentException("该财务分类已经被使用，不可删除！");
		}
		return super.removeById(id);
	}

	private void normalizePath(BillCategory dto) {
		if (StrUtil.isBlank(dto.getPath())) {
			dto.setPath("");
			dto.setPid(0);
			return;
		}
		String p = dto.getPath().trim();
		if (!p.startsWith("/")) {
			p = "/" + p;
		}
		if (!p.endsWith("/")) {
			p = p + "/";
		}
		dto.setPath(p);
		List<String> parts = StrUtil.splitTrim(p, "/");
		if (!parts.isEmpty()) {
			try {
				dto.setPid(Integer.parseInt(parts.get(parts.size() - 1)));
			}
			catch (NumberFormatException e) {
				dto.setPid(0);
			}
		}
		else {
			dto.setPid(0);
		}
	}

	private int calcLevel(String path) {
		if (StrUtil.isBlank(path)) {
			return 1;
		}
		return StrUtil.splitTrim(path, '/').size();
	}

	private String generateNo(int pid, Long entid) {
		String parentNo = "";
		if (pid > 0) {
			BillCategory p = baseMapper.selectById((long) pid);
			if (ObjectUtil.isNull(p)) {
				throw new IllegalArgumentException("分类信息获取异常");
			}
			parentNo = StrUtil.nullToEmpty(p.getCateNo());
		}
		long n = count(Wrappers.lambdaQuery(BillCategory.class)
			.eq(BillCategory::getPid, pid)
			.eq(BillCategory::getEntid, entid));
		return parentNo + String.format("%02d", n + 1);
	}

}
