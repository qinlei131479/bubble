package com.bubblecloud.biz.oa.service.impl;

import org.springframework.stereotype.Service;
import com.bubblecloud.common.core.util.R;
import org.springframework.transaction.annotation.Transactional;

import com.bubblecloud.biz.oa.mapper.DictTypeMapper;
import com.bubblecloud.biz.oa.service.DictTypeService;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.DictType;

/**
 * 字典类型服务实现。
 *
 * @author qinlei
 * @date 2026/3/29 下午12:00
 */
@Service
public class DictTypeServiceImpl extends UpServiceImpl<DictTypeMapper, DictType> implements DictTypeService {

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(DictType req) {
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(DictType req) {
		return super.update(req);
	}

}
