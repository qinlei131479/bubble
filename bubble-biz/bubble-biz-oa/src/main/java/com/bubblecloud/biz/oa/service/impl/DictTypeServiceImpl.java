package com.bubblecloud.biz.oa.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.mapper.DictTypeMapper;
import com.bubblecloud.biz.oa.service.DictTypeService;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.DictType;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 字典类型服务实现。
 *
 * @author qinlei
 * @date 2026/3/29 下午12:00
 */
@Service
public class DictTypeServiceImpl extends UpServiceImpl<DictTypeMapper, DictType> implements DictTypeService {

	@Override
	public Page<DictType> pageDictTypes(long current, long size, String name, String linkType, Integer status) {
		var q = Wrappers.lambdaQuery(DictType.class);
		if (StrUtil.isNotBlank(name)) {
			q.like(DictType::getName, name);
		}
		if (StrUtil.isNotBlank(linkType)) {
			q.eq(DictType::getLinkType, linkType);
		}
		if (ObjectUtil.isNotNull(status)) {
			q.eq(DictType::getStatus, status);
		}
		q.orderByDesc(DictType::getId);
		return this.page(new Page<>(current, size), q);
	}

}
